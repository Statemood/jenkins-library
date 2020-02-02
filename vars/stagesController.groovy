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

def preProcess(stage_id=1) {
    stage("Stage $stage_id: Pre-Process") {
        // Set default info
        // Set build info
        // Check parameters

        log.i "Stage Pre-Process OK"
    }
}

def gitClone(stage_id=2) {
    stage("Stage $stage_id: Git Clone") {
        def gitco = new Git()
        
        gitco.clone(Config.data['repo'], Config.data['revision'])
    }
}

def sonar(stage_id=3) {
    stage("Stage $stage_id: SonarQube Scanner") {
        def sonar = new SonarQube()
        sonar.scanner()
    }
}

def compile(stage_id=4) {
    stage("Stage $stage_id: Build Code") {
        def language = new Language()
        language.seletor(Config.data['lang'])
    }
}

def testJunit(stage_id=5) {
    def private tcj = Config.data['build_command_test_junit']
    if (tcj) {
        stage("Stage $stage_id: Junit Test") {
            log.i "Test by command: " + tcj

            sh(tcj)
        }
    }
}

def dockerStage(){
    def private df = new DockerFile()
    def private di = new DockerImage()

    def image = DOCKER_REGISTRY + '/' + PROJECT_NAME + '/' + APP_NAME + ':' + GIT_REVISION
    df.generate()
    di.build(image)
    di.push(image)
}

return this