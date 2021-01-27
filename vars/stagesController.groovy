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
import me.rulin.k8s.Kubernetes

def call(Map args = [:]) {
    /*
    Order:
        1. Local Settings
        2. .jenkins.yaml
        3. Config
    */

    loadSettings()

    if(!ACTION) { ACTION = "deploy" }

    Config.data['repo']             = args.containsKey('repo')              ?: null
    Config.data['revision']         = args.containsKey('revision')          ?: GIT_REVISION
    Config.data['language']         = args.containsKey('language')          ?: "java"
    Config.data['action']           = ACTION
    Config.data['build.user']       = BUILD_USER
    Config.data['env']              = ENVIRONMENT
    Config.data['credentials.id']   = args.containsKey('credentials.id')    ?: "DefaultGitSCMCredentialsID"

    Config.data += args 

    println Config.data 
    println args 

    log.i "Using workspace: " + FIRST_DIR

    stagePreProcess()
    stageGitClone()
    stageCompile()
    stageTest()
    stageDocker()
    stageKubernetes()
}

// Set build info
def stageCurrentBuildInfo(){
    def private name = BUILD_NUMBER + "-" + Config.data['env']
    def private desc = Config.data['action'] + " by " + Config.data['build.user'] + ", version " + Config.data['revision'] 

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

def stageCompile() {
    stage("Build Code") {
        node(STAGE_BUILD) {
            dir(FIRST_DIR) {
                def language = new Language()
                language.seletor(Config.data['language'])
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
    stage("Build Image")
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
                def k8s = new Kubernetes()

                k8s.updateYaml()
            }
        }
    }
}

return this