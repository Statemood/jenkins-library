/* init.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def call(body){
    log.info "Initial Pipeline"

    //dockerCmd   = new Docker()
    //dockerCmd.images()
    //dockerCmd.version()

    loadLocalSettings()

    echo "Settings loaded"

    echo "Registry Password: " + DOCKER_REGISTRY_PASSWORD
    echo "STAGE: " + STAGE_PRE_PROCESS

    stages.controller()
}

def loadLocalSettings(){
    if (SETTINGS) {
        try {
            if (fileExists(SETTINGS)) {
                log.info "Loading local settings"

                load(SETTINGS)
            }
            else {
                log.warning "File not found: $SETTINGS"
            }
        }
        catch (e) {
            throw e 
        }
    }
}