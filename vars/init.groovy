/* init.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def go(Map parameters = [:]){
    log.info "Initial Pipeline"

    def Map settings = [:]
    settings = ["repo": "123.git"]

    git_defined = settings['repo']

    git_new = parameters['repo']

    echo "Defined git: " + git_defined
    echo "New git: " + git_new

    parameters.put('repo', git_defined)

    println parameters

    loadLocalSettings()
}

def startup(){

    stages.controller()
}

def loadLocalSettings(){
    if (SETTINGS) {
        try {
            if (fileExists(SETTINGS)) {
                log.info "Loading local settings"

                load(SETTINGS)

                log.info "Local settings loaded"
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