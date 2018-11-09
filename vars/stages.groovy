/* stages.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def preProcess() {
    node(STAGE_PRE_PROCESS) {
        // Set default info

        // Set build info

        // Check parameters
    }
}

def git(){
    node(STAGE_GIT) {
        checkoutCode()
    }
}

def docker(){
    node(STAGE_DOCKER) {

    }
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