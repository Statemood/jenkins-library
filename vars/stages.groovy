/* stages.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import me.rulin.jenkins.Build

def controller() {
    node(STAGE_PRE_PROCESS) {

    }

    node(STAGE_GIT) {

    }

    node(STAGE_DOCKER) {

    }

    node(STAGE_KUBERNETES) {

    }

    node(STAGE_POST_PROCESS) {

    }
}

def stageBuild() {
    node(STAGE_BUILD) {

    }
}
return this