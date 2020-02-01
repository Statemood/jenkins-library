/* loadLocalSettings.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def call(){
    def local_data = readFromYaml()

    if (SETTINGS != null) {
        try {
            if (fileExists(SETTINGS)) {
                log.i "Loading local settings"

                load(SETTINGS)
            }
            else {
                log.w "File not found: " + SETTINGS
            }
        }
        catch (e) {
            throw e
        }
    }
    log.i "Load defaults"
    defaults()
}

def readFromYaml() {
    private yf = 'jenkins.yaml'
    if (fileExists(yf)) {
        try {
            log.i "Read config from " + yf
            def yaml_data = readYaml file: yf

            return yaml_data
        }
        catch (e) {
            throw e
        }
    }
}

def readFromJson() {
    private jf = 'jenkins.json'
    if (fileExists(jf)) {
        try {
            log.i "Read config from " + jf
            json_data = readJSON file: jf

            return json_data
        }
        catch (e) {
            throw e
        }
    }
}

def defaults(){
    Config.data['build_command_test_junit']     = "mvn test"
}