/* stagesController.groovy
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
import me.rulin.kubernetes.Json
import me.rulin.kubernetes.Yaml

// Set build info
def stageCurrentBuildInfo(){
    def private name = BUILD_NUMBER + "-" + Config.data['env']
    def private desc = Config.data['build.user'] + " " + Config.data['action'] + " " + Config.data['revision'] 

    currentBuild.displayName = name 
    currentBuild.description = desc 
}

def stagePreProcess() {
    stage("Pre-Process") {
        node(STAGE_PRE_PROCESS) {
            dir(FIRST_DIR) {
                // Set default info
                // Set build info
                // Check parameters
                stageCurrentBuildInfo()
                log.i "Stage Pre-Process OK"
            }
        }
    }
}

def stageGitClone() {
    stage("Git Clone") {
        node(STAGE_GIT) {
            dir(FIRST_DIR) {
                try {
                    def private revision = Config.data['revision']
                    def private     repo = Config.data['repo']

                    log.i "Git clone " + revision + " " + repo

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

def stageSonar() {
    stage("SonarQube Scanner") {
        node(STAGE_SONAR) {
            dir(FIRST_DIR) {
                def sonar = new SonarQube()
                sonar.scanner()
            }
        }
    }
}

def stageBuild() {
    stage("Build Code") {
        lang = Config.data['language'].toLowerCase()

        node(lang) {
            dir(FIRST_DIR) {
                def language = new Language()
                language.seletor(lang)
            }
        }
    }
}

def stageTest() {
    def private utc = Config.data['build.command.unit.test']
    if (utc) {
        stage("Unit Test") {
            node(STAGE_TEST) {
                dir(FIRST_DIR) {
                    log.i "Test by command: " + utc

                    sh(utc)
                }
            }
        }
    }
}

def stageDocker(){
    // if (DEPLOY_MODE == "Container") {}
    stage("Build Image") {
        node(STAGE_DOCKER) {
            dir(FIRST_DIR) {
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

def stageKubernetes(){
    stage("Kubernetes") {
        node(STAGE_K8S) {
            dir(FIRST_DIR) {
                def gen = new Yaml()

                gen.deployment()
            }
        }
    }
}

return this