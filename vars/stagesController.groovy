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
import me.rulin.ci.DockerFile
import me.rulin.ci.DockerImage

def preProcess() {
    stage("Pre-Process") {
        // Set default info
        // Set build info
        // Check parameters

        log.i "Stage Pre-Process OK"
    }
}

def gitClone() {
    stage("Git Clone") {
        def gitco = new Git()
        
        gitco.clone(Config.data['repo'], Config.data['revision'])
    }
}

def sonar() {
    stage("SonarQube Scanner") {
        def sonar = new SonarQube()
        sonar.scanner()
    }
}

def compile() {
    stage("Build Code") {
        def language = new Language()
        language.seletor(Config.data['language'])
    }
}

def testJunit() {
    def private tcj = Config.data['build_command_test_junit']
    if (tcj) {
        stage("Junit Test") {
            log.i "Test by command: " + tcj

            sh(tcj)
        }
    }
}

def dockerStage(){
    def private  df = new DockerFile()
    def private  di = new DockerImage()
    def private git = new Git()

    def private tag = GIT_REVISION + '-' + git.commitIDShort(8)

    def image = DOCKER_REGISTRY + '/' + PROJECT_NAME + '/' + APP_NAME + ':' + tag 
    
    echo "Tag: " + tag 
    echo "Img: " + image

    df.generate()
    di.build(image)
    di.push(image)
}

return this