/* loadLocalSettings.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def call(){
    local_data = readFromYaml()

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

def readFromYaml() {
    private yf = 'ci.jenkins.yaml'
    if (fileExists(yf)) {
        try {
            log.info "Read config from " + yf
            yaml_data = readYaml file: yf

            return yaml_data
        }
        catch (e) {
            return false
        }
    }
}

def readFromJson() {
    private jf = 'ci.jenkins.json'
    if (fileExists(jf)) {
        try {
            log.info "Read config from " + jf
            json_data = readJSON file: jf

            return json_data
        }
        catch (e) {
            return false
        }
    }
}