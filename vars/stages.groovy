/* stages.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import me.rulin.ci.Git
import me.rulin.ci.SonarQube

def controller(){
    echo "Stage controller"
    println ciConfig.parameters

    preProcess()
    git()
    if (APP_LANG == "java") {
        compile(3)
        //sonar(4)
        test(4)
    }
    else {
        //sonar()
        compile()
        test()
    }
}

def preProcess(stage_id=1) {
    node(STAGE_PRE_PROCESS) {
        stage("Stage $stage_id: Pre-Process") {
            // Set default info

            // Set build info

            // Check parameters

            echo "Stage Pre-Process OK"
        }
    }
}

def git(stage_id=2) {
    node(STAGE_GIT) {
        stage("Stage $stage_id: Git Checkout") {
            echo "stage git"
            println ciConfig.parameters
            echo "echo done"
            git = new Git()
            git.checkout(repo, SCM_REVISION)
        }
    }
}

def sonar(stage_id=3) {
    node(STAGE_SONAR) {
        stage("Stage $stage_id: SonarQube Scanner") {
            sonar = new SonarQube()
            sonar.scan()
        }
    }
}

def compile(stage_id=4) {
    node(STAGE_BUILD) {
        stage("Stage $stage_id: Build Code") {
            build.controller()
        }
    }
}

def test(stage_id=5) {
    node(STAGE_TEST) {
        stage("Stage $stage_id: Test") {
            if (test_cmd) {
                log.info "Test by command: $test_cmd"

                sh(test_cmd)
            }

            return
        }
    }
}

node(STAGE_DOCKER) {
    docker.controller()
}

node(STAGE_KUBERNETES) {

}

node(STAGE_POST_PROCESS) {

}

return this