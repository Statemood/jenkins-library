/* stages.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import me.rulin.ci.Git 

def controller(){
    preProcess()
    git()
    if (APP_LANG == "java") {
        build(3)
        //sonar(4)
    }
    else {
        //sonar()
        build()
    }
}

def preProcess(stage_id=1) {
    node(STAGE_PRE_PROCESS) {
        stage("Stage $stage_id: Pre-Process")
        // Set default info

        // Set build info

        // Check parameters

        echo "Stage Pre-Process OK"
    }
}

def git(stage_id=2) {
    node(STAGE_GIT) {
        stage("Stage $stage_id: Git Checkout") {

            scmGit = new Git()
            scmGit.checkout(repo, SCM_REVISION)
        }
    }
}

def sonar(stage_id=3) {
    node(STAGE_SONAR) {
        stage("Stage $stage_id: SonarQube Scanner") {
            sonar.scanner()
        }
    }
}

def build(stage_id=4) {
    node(STAGE_BUILD) {
        stage("Stage $stage_id: Build Code") {
            build.controller()
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