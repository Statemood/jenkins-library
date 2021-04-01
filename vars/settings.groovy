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
        2. Default Settings (@settings.config)
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
        if(!ACTION)             { ACTION            = "deploy"  }
        if(!ENVIRONMENT)        { ENVIRONMENT       = "dev"     }
        if(!GIT_REVISION)       { GIT_REVISION      = null      }
        if(!DEPLOYMENT_MODE)    { DEPLOYMENT_MODE   = null      }
    }
    catch (e) {
        log.err "在初始化环境变量时遇到问题."
        throw e
    }

    Config.settings = [
        artifact_src                    : '.',
        artifact_dest                   : '.',
        base_action                     : ACTION,
        base_dir                        : metis.getFirstDirectory(),
        base_env                        : ENVIRONMENT,
        base_name                       : metis.getApplicationName(),
        base_port                       : 8080,
        base_project                    : metis.getFrojectName(),
        base_templates_dir              : 'me/rulin/templates/',
        base_web_root                   : '/data/app/',
        base_workspace                  : JENKINS_HOME + '/workspace/' + JOB_NAME,
        build_command                   : 'mvn',
        build_dir                       : '.',
        build_language                  : 'java',
        build_language_version          : 0,
        build_legacy                    : false,
        build_node_name                 : '',
        build_options                   : '',
        build_stage                     : 'master',
        build_user                      : metis.getBuildUserName(),
        build_userid                    : metis.getBuildUserNameID(),
        docker_account                  : 'Registry-Jenkins',
        docker_file                     : 'Dockerfile.jenkins',
        docker_img_name                 : '',
        docker_img_tag                  : '',
        docker_img_build_timeout        : 1800,
        docker_img_build_options        : '',
        docker_img_push_timeout         : 600,
        docker_ignore_file              : '.dockerignore',
        docker_login_timeout            : 15,
        git_commit_length               : 8,
        git_commit_id                   : '',
        git_credentials                 : 'GitLab-Jenkins-UP',
        git_repo                        : null,
        git_revision                    : GIT_REVISION,
        git_stage                       : 'master',
        k8s_allowed_commands            : ['apply', 'create', 'delete', 'get', 'autoscale'],
        k8s_config_dir                  : '/home/jenkins/.kube/',
        k8s_img_pull_policy             : 'IfNotPresent',
        k8s_img_pull_secret             : 'images-puller-' + metis.getFrojectName(),
        k8s_min_ready_seconds           : 60,
        k8s_namespace                   : metis.getFrojectName(),
        k8s_progress_deadline_sec       : 300,
        k8s_replicas                    : metis.getReplicasNumber(),
        k8s_res_limits_cpu              : '300m',
        k8s_res_limits_memory           : '512Mi',
        k8s_res_requests_cpu            : '300m',
        k8s_res_requests_memory         : '512Mi',
        k8s_rev_history_limit           : 5,
        k8s_strategy_max_surge          : '25%',
        k8s_strategy_max_unavailable    : '25%',
        k8s_termination_GPS             : 60,
        k8s_yml_default_dir             : 'kubernetes/yaml/default/',
        k8s_yml_default_deploy          : 'deployment.yaml',
        k8s_yml_default_svc             : 'service.yaml',
        notice_timeout                  : 15,
        run_command                     : '',
        run_user                        : 1000,
        run_by_nginx                    : false,
        skip_build                      : false,
        skip_docker                     : false,
        skip_git                        : false,
        skip_k8s                        : false,
        skip_sonar                      : true,
        skip_test                       : true,
        ssh_host                        : null,
        ssh_speed_limit                 : 20480,
        ssh_user                        : 'www',
        ssh_port                        : 22,
        test_command                    : 'mvn',
        test_options                    : 'test',
        test_type                       : 'unit'
    ]

    return Config.settings
}

def localSettingsFile(){
    try {
        if (SETTINGS) {
            if (fileExists(SETTINGS)) {
                //log.output('info', 'init_load_settings_file')
                log.i 'Load settings file.'

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
}

return this