/* loadLocalSettings.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def merge(Map arguments=[:]){
    /*
    Order:
        1. Local Settings
        2. Default Settings (@ settings.config)
        2. .jenkins.yaml
        3. User Config
    */
    localSettingsFile()
    config()

    Config.data = Config.settings + arguments

    println '*********** Config.settings ***********'
    println Config.settings
    
    println '*********** config.data ***********'
    println Config.data

    println '*********** arguments ***********'
    println arguments
}

def config(){
    // 设置初始化配置
    try {
        if(!ACTION)         { ACTION        = "deploy"  }
        if(!ENVIRONMENT)    { ENVIRONMENT   = "dev"     }
        if(!GIT_REVISION)   { GIT_REVISION  = null      }
    }
    catch (e) {
        log.err "在初始化环境变量时遇到问题."
        throw e
    }

    Config.settings = [
        'base'                      : [
            'dir'                   : metis.getFirstDirectory(),
            'env'                   : ENVIRONMENT,
            'name'                  : metis.getApplicationName(),
            'port'                  : 8080,
            'workspace'             : JENKINS_HOME + "/workspace/" + JOB_NAME
        ],
        'build'                     : [
            'action'                : ACTION,
            'command'               : "mvn",
            'dir'                   : ".",
            'legacy'                : false,
            'options'               : "-ff -ntp -U clean -Dmaven.test.skip=true package dependency:tree",
            'skip'                  : false,
            'stage'                 : "master",
            'user'                  : metis.getBuildUserName(),
            'userid'                : metis.getBuildUserNameID()
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
            'namespace'             : metis.getFrojectName(),
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
        throw e
        log.err "Oops! An error occurred while try to load default settings."
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