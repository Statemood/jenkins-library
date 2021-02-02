/* controller.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

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
    codeBuild()
    codeTest()
    doDocker()
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
        node(STAGE_GIT) {
            dir(Config.data.base_dir) {
                try {
                    def private revision = Config.data.git_revision
                    def private     repo = Config.data.git_repo

                    log.i 'Git clone ' + revision + ' from ' + repo

                    git credentialsId: Config.data.git_credentials,
                        branch: revision,
                        url: repo

                    Config.data.git_commit_id = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                } catch (e) {
                    log.e 'Ops! Error occurred during git checkout'
                    throw e
                }
            }
        }
    }
}

def sonarScan() {
    stage('SonarQube Scanner') {
        node(STAGE_SONAR) {
            dir(Config.data.base_dir) {
                def sonar = new SonarQube()
                sonar.scanner()
            }
        }
    }
}

def codeBuild() {
    stage('Build Code') {
        lang = Config.data.build_language.toLowerCase()

        node(lang) {
            dir(Config.data.base_dir) {
                def language = new Language()
                language.seletor(lang)
            }
        }
    }
}

def codeTest() {
    def private utc = Config.data.
    if (utc) {
        stage('Unit Test') {
            node(STAGE_TEST) {
                dir(Config.data.base_dir) {
                    log.i 'Test by command: ' + utc

                    sh(utc)
                }
            }
        }
    }
}

def doDocker(){
    // if (DEPLOY_MODE == 'Container') {}
    stage('Docker') {
        node(STAGE_DOCKER) {
            dir(Config.data.base_dir) {
                def private  docker = new Docker()
                def private     cfg = Config.data
                def cfg.docker_img_tag  = cfg.git_revision + '-' + cfg.git_commit_id[0..8]
                def cfg.docker_img_name = DOCKER_REGISTRY  + '/' + cfg.base_project + '/' + cfg.base_name + ':' + cfg.docker_img_tag 
                def cfg.docker_img      = cfg.docker_img_name + ':' + cfg.docker_img_tag
                docker.genDockerfile()
                docker.build(cfg.docker_img)
                docker.login()
                docker.push(cfg.docker_img)
            }
        }
    }
}

def doKubernetes(){
    stage('Kubernetes') {
        node(STAGE_K8S) {
            dir(Config.data.base_dir) {
                def gen = new Yaml()
                def pth = Config.data.k8s_standard_templates_dir
                gen.deployment(pth + '/deployment.yaml')
                gen.service(pth + '/service.yaml')

                log.a 'Ready to deploy!'
            }
        }
    }
}

return this