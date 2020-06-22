/* loadLocalSettings.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def call(){
    try {
        if (SETTINGS) {
            if (fileExists(SETTINGS)) {
                log.i "Loading local settings"

                load(SETTINGS)
            }
            else {
                log.w "File not found: " + SETTINGS
            }
        }
        else {
             log.w "Undefined 'SETTINGS'"
        }
    }
    catch (e) {
        log.w "Undefined 'SETTINGS'"
    }

    log.i "Load defaults"
    defaults()

    sf_lists = ['settings.yaml', '.jenkins.yaml']
    for (sf in sf_lists) {
        readFromYaml(sf)
    }
}

def readFromYaml(yaml_file) {
    if (fileExists(yaml_file)) {
        try {
            log.i "Read config from " + yaml_file
            def yaml_data = readYaml file: yaml_file

            return yaml_data
        }
        catch (e) {
            throw e
        }
    }
}

def defaults(){
    Config.data['build.command.unit.test']     = "mvn test"
}