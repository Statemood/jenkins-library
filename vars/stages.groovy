/* stages.groovy
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
        gitco = new Git()
        gitco.clone(Config.data['app.repo'], Config.data['app.repo.revision'])
    }
}

def sonar(stage_id=3) {
    stage("Stage $stage_id: SonarQube Scanner") {
        sonar = new SonarQube()
        sonar.scanner()
    }
}

def compile(stage_id=4) {
    stage("Stage $stage_id: Build Code") {
        language = new Language()
        language.seletor(Config.data['app.lang'])
    }
}

def testJunit(stage_id=5) {
    private tcj = Config.data['app.build.command.test.junit']
    if (tcj) {
        stage("Stage $stage_id: Junit Test") {
            log.i "Test by command: " + tcj

            sh(tcj)
        }
    }
}

return this