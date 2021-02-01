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

    Config.settings = [
        'base'                      : [
            'dir'                   : first_dir,
            'name'                  : app_name,
            'port'                  : 8080,
            'workspace'             : JENKINS_HOME + "/workspace/" + JOB_NAME
        ],
        'build'                     : [
            'action'                : ACTION,
            'command'               : "mvn",
            'dir'                   : ".",
            'env'                   : ENVIRONMENT,
            'legacy'                : false,
            'options'               : "-ff -ntp -U clean -Dmaven.test.skip=true package dependency:tree",
            'skip'                  : false,
            'stage'                 : "master",
            'user'                  : BUILD_USER,
            'userid'                : BUILD_USER_ID
        ],
        'docker'                    : [
            'account'               : "Docker-Registry",
            'image'                 : [
                'name'              : "",
                'tag'               : "",
                'build_timeout'     : 1800,
                'build_options'     : "",
                'push_timeout'      : 600
            ],
            'ignore_file'           : ".dockerignore"
        ],
        'git'                       : [
            'commit_length'         : 8,
            'credentials'           : "DefaultGitSCMCredentialsID",
            'repo'                  : null,
            'revision'              : GIT_REVISION,
            'skip'                  : false,
            'stage'                 : "master"
        ],
        'k8s'                       : [
            'namespace'             : project_name,
            'resources'             : [
                'limits_cpu'        : "300m",
                'limits_memory'     : "512Mi",
                'requests_cpu'      : "300m",
                'requests_memory'   : "512Mi"
            ],
            'run'                   : [
                'args'              : null,
                'command'           : null,
                'user'              : null
            ]
        ]
    ]

    return Config.settings
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