/* stages.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import me.rulin.ci.Git
import me.rulin.ci.SonarQubeScanner

def call(Map args = [:]) {
    Config.data  += args

    loadSettings()
    preProcess()
    gitClone()
    if (Config.data['lang'] == "java") {
        compile(3)
        test(4)
    }
    else {
        compile()
        test()
    }
}

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

        gitco.checkout(Config.data['repo'], Config.data['revision'])
    }
}

def sonar(stage_id=3) {
    stage("Stage $stage_id: SonarQube Scanner") {
        sonar = new SonarQubeScanner()
        sonar.scan()
    }
}

def compile(stage_id=4) {
    stage("Stage $stage_id: Build Code") {
        build.controller()
    }
}

def test(stage_id=5) {
    stage("Stage $stage_id: Test") {
        test_cmd = Config.data['test_cmd']
        if (test_cmd) {
            log.i "Test by command: $test_cmd"

            sh(test_cmd)
        }
        return
    }
}

return this