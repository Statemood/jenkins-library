/* loadLocalSettings.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def merge(Map args=[:]){
    /*
    Order:
        1. Local Settings
        2. .jenkins.yaml
        3. Config
    */
    localSettingsFile()

    Config.data = defaultSettings() + args
}

def defaultSettings(){
    // 设置初始化配置
    try {
        def first_dir  = pwd()

        if(!ACTION)         { ACTION        = "deploy"  }
        if(!ENVIRONMENT)    { ENVIRONMENT   = "dev"     }
        if(!GIT_REVISION)   { GIT_REVISION  = null      }

        /* Get User info
           Requeire 'build user vars' plugin
           See https://plugins.jenkins.io/build-user-vars-plugin for more information
        */
        wrap([$class: 'BuildUser']) { build_user    = BUILD_USER    }
        wrap([$class: 'BuildUser']) { build_user_id = BUILD_USER_ID }

        def project_name        = JOB_BASE_NAME.split('_')[0].toLowerCase()
        def     app_name        = JOB_BASE_NAME.split('_')[1].toLowerCase()
    }
    catch (e) {
        log.err "在初始化环境变量时遇到问题."
        throw e
    }
}

def localSettingsFile(){
    try {
        if (DEFAULT_SETTINGS) {
            if (fileExists(DEFAULT_SETTINGS)) {
                log.i "Loading local settings"

                load(DEFAULT_SETTINGS)
            }
            else {
                log.w "File not found: " + DEFAULT_SETTINGS
            }
        }
        else {
             log.w "Undefined 'DEFAULT_SETTINGS'"
        }
    }
    catch (e) {
        throw e
        log.err "Oops! An error occurred while try to load 'DEFAULT_SETTINGS'"
    }

    log.i "Load defaults "

    sf_lists = ['settings.yaml', 'jenkins.yaml']
    for (sf in sf_lists) {
        if (fileExists(sf)) {
            try {
                log.i "Read config from " + sf
                def yaml_data = readYaml file: sf
                
                Config.data += yaml_data

                c = Config.data + yaml_data
                print c
                print yaml_data
            }
            catch (e) {
                throw e
            }
        }
    }
}

return this