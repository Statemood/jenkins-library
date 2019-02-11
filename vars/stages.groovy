/* stages.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/


node(STAGE_PRE_PROCESS) {
    // Set default info

    // Set build info

    // Check parameters
}

node(STAGE_GIT) {
    base.gitCheckout()
}

node(STAGE_BUILD) {
    build.Build()
}

node(STAGE_DOCKER) {
    docker.dockerfileGenerate()
    
}

node(STAGE_KUBERNETES) {

}

node(STAGE_POST_PROCESS) {

}

return this