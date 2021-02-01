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

    println Config.settings
    println '-----' 
    println Config.settings['base']

    println Config.data 
    println args 

    log.i "Using workspace: " + Config.data['base']['dir']

    preProcess()
    codeClone()
    codeBuild()
    codeTest()
    doDocker()
    doKubernetes()
}

// Set build info
def currentBuildInfo(){
    def private name = BUILD_NUMBER + "-" + Config.data['env']
    def private desc = Config.data['build.user'] + " " + Config.data['action'] + " " + Config.data['revision'] 

    currentBuild.displayName = name 
    currentBuild.description = desc 
}

def preProcess() {
    stage("Pre-Process") {
        node(STAGE_PRE_PROCESS) {
            dir(Config.data['base']['dir']) {
                // Set default info
                // Set build info
                // Check parameters
                currentBuildInfo()
                log.i "Stage Pre-Process OK"
            }
        }
    }
}

def codeClone() {
    stage("Git Clone") {
        node(STAGE_GIT) {
            dir(Config.data['base']['dir']) {
                try {
                    def private revision = Config.data['revision']
                    def private     repo = Config.data['repo']

                    log.i "Git clone " + revision + " from " + repo

                    git credentialsId: Config.data['credentials.id'],
                        branch: revision,
                        url: repo

                    Config.data['commit.id'] = sh(script: "git rev-parse HEAD", returnStdout: true).trim()

                    return
                } catch (e) {
                    log.e "Ops! Error occurred during git checkout"
                    throw e
                }
            }
        }
    }
}

def sonarScan() {
    stage("SonarQube Scanner") {
        node(STAGE_SONAR) {
            dir(Config.data['base']['dir']) {
                def sonar = new SonarQube()
                sonar.scanner()
            }
        }
    }
}

def codeBuild() {
    stage("Build Code") {
        lang = Config.data['language'].toLowerCase()

        node(lang) {
            dir(Config.data['base']['dir']) {
                def language = new Language()
                language.seletor(lang)
            }
        }
    }
}

def codeTest() {
    def private utc = Config.data['build.command.unit.test']
    if (utc) {
        stage("Unit Test") {
            node(STAGE_TEST) {
                dir(Config.data['base']['dir']) {
                    log.i "Test by command: " + utc

                    sh(utc)
                }
            }
        }
    }
}

def doDocker(){
    // if (DEPLOY_MODE == "Container") {}
    stage("Build Image") {
        node(STAGE_DOCKER) {
            dir(Config.data['base']['dir']) {
                def private  docker = new Docker()
                def private  tag = GIT_REVISION    + '-' + Config.data['commit.id'][0..8]
                env.DOCKER_IMAGE = DOCKER_REGISTRY + '/' + PROJECT_NAME + '/' + APP_NAME + ':' + tag 

                docker.genDockerfile()
                docker.build(DOCKER_IMAGE)
                docker.login()
                docker.push(DOCKER_IMAGE)
            }
        }
    }
}

def doKubernetes(){
    stage("Kubernetes") {
        node(STAGE_K8S) {
            dir(Config.data['base']['dir']) {
                def gen = new Yaml()
                def pth = 'me/rulin/templates/kubernetes/yaml/standard'
                gen.deployment(pth + '/deployment.yaml')
                gen.service(pth + '/service.yaml')

                log.a "Ready to deploy!"
            }
        }
    }
}

return this