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

    echo "Workspace: " + WS

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