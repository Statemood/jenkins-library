/* init.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def settings(){
    if(SETTINGS){
        if(fileExists(SETTINGS)){
            log.info "Loading local settings"

            load(SETTINGS)
        }
    }
}

return this