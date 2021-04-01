/* controller.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import me.rulin.cd.Server
import me.rulin.ci.Git
import me.rulin.ci.Language
import me.rulin.ci.SonarQube
import me.rulin.docker.Docker
import me.rulin.kubernetes.Command
import me.rulin.kubernetes.Yaml

def entry(Map args = [:]) {
    /*
    Order:
        1. Local Settings
        2. .jenkins.yaml
        3. Config
    */

    settings.merge(args)

    preProcess()
    codeClone()

    def private scan_after_build = ['java']

    if(Config.data.build_language in scan_after_build) {
        codeBuild()
    
        parallel (
            'Test'          : { codeTest()  },
            'Sonar Scan'    : { sonarScan() },
            'Docker'        : { doDocker()  }
        )
    }
    else {
        sonarScan()
        codeBuild()
        parallel (
            'Test'          : { codeTest()  },
            'Sonar Scan'    : { sonarScan() },
            'Docker'        : { doDocker()  }
        )
    }

    doKubernetes()
}

// Set build info
def currentBuildInfo(){
    def private name = BUILD_NUMBER + '-' + Config.data.base_env
    def private desc = Config.data.build_user + ' ' + Config.data.base_action + ' ' + Config.data.git_revision

    currentBuild.displayName = name 
    currentBuild.description = desc 
}

def preProcess() {
    stage('Pre-Process') {
        node(STAGE_PRE_PROCESS) {
            dir(Config.data.base_dir) {
                // Set default info
                // Set build info
                // Check parameters
                currentBuildInfo()
                log.i 'Stage Pre-Process OK'
            }
        }
    }
}

def codeClone() {
    stage('Git Clone') {
        if(!Config.data.skip_git){
            node(STAGE_GIT) {
                dir(Config.data.base_dir) {
                    try {
                        def private revision = Config.data.git_revision
                        def private     repo = Config.data.git_repo

                        log.i 'Git clone ' + revision + ' from ' + repo

                        git credentialsId: Config.data.git_credentials,
                            branch: revision,
                            url: repo

                        metis.getGetCommitID()
                    } catch (e) {
                        log.e 'Ops! Error occurred during git checkout'
                        throw e
                    }
                }
            }
        }
        else {
            log.a 'Skipped stage git clone.'
        }
    }
}

def sonarScan() {
    stage('SonarQube Scanner') {
        if(!Config.data.skip_sonar){
            node(STAGE_SONAR) {
                dir(Config.data.base_dir) {
                    def sonar = new SonarQube()
                    sonar.scanner()
                }
            }
        }
        else {
            log.a 'Sikpped stage SonarQube Scan'
        }
    }
}

def qualityGate(){
    stage("Quality Gate") {
        node(STAGE_SONAR) {
            timeout(time: 30, unit: 'MINUTES') {
                dir(Config.data.base_dir) {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}

def codeBuild() {
    stage('Build Code') {
        def private      lang = Config.data.build_language.toLowerCase()
        def private   version = Config.data.build_language_version
        def private node_name = lang

        if(version != 0){ node_name = lang + '-' + version }

        Config.data.build_node_name = node_name

        node(node_name) {
            dir(Config.data.base_dir) {
                def language = new Language()
                language.seletor(lang)
            }
        }
    }
}

def codeTest() {
    def private utc = Config.data.test_command + ' ' + Config.data.test_options
    if (!Config.data.skip_test) {
        stage('Test') {
            node(Config.data.build_node_name = node_name) {
                dir(Config.data.base_dir) {
                    log.i 'Test by command: ' + utc

                    sh(utc)
                }
            }
        }
    }
}

def doDocker(){
    if(DEPLOYMENT_MODE == 'Legacy') { return }
    stage('Docker') {
        node(STAGE_DOCKER) {
            dir(Config.data.base_dir) {
                def private  docker = new Docker()
                def private     cfg = Config.data
                Config.data.docker_img_tag  = cfg.git_revision    + '-' + cfg.git_commit_id[0..8]
                Config.data.docker_img_name = DOCKER_REGISTRY     + '/' + cfg.base_project + '/' + cfg.base_name
                Config.data.docker_img      = cfg.docker_img_name + ':' + cfg.docker_img_tag

                parallel (
                    'Generate Dockerfile': { docker.genDockerfile() },
                    'Login to Registry'  : { docker.login() }
                )
                
                docker.build(cfg.docker_img)
                docker.push(cfg.docker_img)
            }
        }
    }
}

def doKubernetes(){
    if(DEPLOYMENT_MODE == 'Legacy') { return }
    stage('Kubernetes') {
        node(STAGE_K8S) {
            dir(Config.data.base_dir) {
                def cmd     = new Command()
                def gen     = new Yaml()
                def pth     = Config.data.k8s_yml_default_dir
                def deploy  = Config.data.k8s_yml_default_deploy
                def service = Config.data.k8s_yml_default_svc

                parallel (
                    'Generate Deployment'   : { gen.deployment() },
                    'Generate Service'      : { gen.service()    }
                )
                log.a 'Ready to deploy!'

                parallel (
                    'Deploy Deployment'     : { cmd.command('apply', pth + deploy)  },
                    'Deploy Service'        : { cmd.command('apply', pth + service) }
                )
            }
        }
    }
}

def doServer(List th=[]){
    if(DEPLOYMENT_MODE == 'Container') { return }

    def server = new Server()

    for(host in th){
        server.upload()
    }
}

return this