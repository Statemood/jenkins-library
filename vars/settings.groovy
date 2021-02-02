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
        base_action                     : ACTION,
        base_dir                        : metis.getFirstDirectory(),
        base_env                        : ENVIRONMENT,
        base_name                       : metis.getApplicationName(),
        base_port                       : 8080,
        base_project                    : metis.getFrojectName(),
        base_web_root                   : '/data/app',
        base_workspace                  : JENKINS_HOME + '/workspace/' + JOB_NAME,
        build_command                   : 'mvn',
        build_dir                       : '.',
        build_language                  : 'java',
        build_legacy                    : false,
        build_options                   : '-ff -ntp -U clean -Dmaven.test.skip=true package dependency:tree',
        build_skip                      : false,
        build_stage                     : 'master',
        build_user                      : metis.getBuildUserName(),
        build_userid                    : metis.getBuildUserNameID(),
        docker_account                  : 'Registry-Jenkins',
        docker_img_name                 : '',
        docker_img_tag                  : '',
        docker_img_build_timeout        : 1800,
        docker_img_build_options        : '',
        docker_img_push_timeout         : 600,
        docker_ignore_file              : '.dockerignore',
        docker_login_timeout            : 15,
        git_commit_length               : 8,
        git_commit_id                   : '',
        git_credentials                 : 'DefaultGitSCMCredentialsID',
        git_repo                        : null,
        git_revision                    : GIT_REVISION,
        git_skip                        : false,
        git_stage                       : 'master',
        k8s_allowed_commands            : ['apply', 'create', 'delete', 'get', 'autoscale'],
        k8s_img_pull_policy             : 'Always',
        k8s_img_pull_secret             : metis.getFrojectName() + '-image-pull-secret',
        k8s_min_ready_seconds           : 60,
        k8s_namespace                   : metis.getFrojectName(),
        k8s_progress_deadline_sec       : 300,
        k8s_replicas                    : metis.getReplicasNumber(),
        k8s_res_limits_cpu              : '300m',
        k8s_res_limits_memory           : '512Mi',
        k8s_res_requests_cpu            : '300m',
        k8s_res_requests_memory         : '512Mi',
        k8s_rev_history_limit           : 5,
        k8s_standard_templates_dir      : 'me/rulin/templates/kubernetes/yaml/standard',
        k8s_strategy_max_surge          : '25%',
        k8s_strategy_max_unavailable    : '25%',
        k8s_termination_GPS             : 60,
        run_args                        : null,
        run_command                     : null,
        run_user                        : null
    ]

    return Config.settings
}

def localSettingsFile(){
    try {
        if (SETTINGS) {
            if (fileExists(SETTINGS)) {
                log.i 'Loading local settings'

                load(SETTINGS)
            }
            else {
                log.w 'File not found: ' + SETTINGS
            }
        }
        else {
             log.w 'Undefined SETTINGS'
        }
    }
    catch (e) {
        throw e
        log.err 'Oops! An error occurred while try to load default settings.'
    }

    log.i 'Load defaults'
}

return this