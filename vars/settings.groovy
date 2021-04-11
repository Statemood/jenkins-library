/* loadLocalSettings.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def config(Map args=[:]){
    /*
    Order:
        1. Local Settings
        2. Default Settings (@settings.config)
        2. .jenkins.yaml
        3. User Config
    */

    defaultSettings()

    if(Config.data.check_permission_by_gitlab){
        def private       s = Config.settings
        def private       d = metis.getGitRepoInfo(args.git_repo_id)
        args.git_repo       = d['http_url_to_repo']
        args.git_name       = d['name']
        args.git_branches   = metis.getGitRepoInfo(args.git_repo_id, '/repository/branches/' + GIT_REVISION)['name']
    }

    def private request_cpu, request_memory, limits_cpu, limits_memory

    requests_cpu    = '0.2'
    requests_memory = '256Mi'
    limits_cpu      = '0.3'
    limits_memory   = '512Mi'

    if(ENVIRONMENT == 'prd'){
        if(s.build_language == 'java'){
            requests_cpu    = '1'
            requests_memory = '4Gi'
            limits_cpu      = requests_cpu
            limits_memory   = requests_memory
        }
        else {
            requests_cpu    = '0.3'
            requests_memory = '512Mi'
            limits_cpu      = requests_cpu
            limits_memory   = requests_memory
        }
    }
    else {
        // Java 
        if(s.build_language == 'java'){
            requests_cpu    = '0.3'
            requests_memory = '512Mi'
            limits_cpu      = '0.5'
            limits_memory   = '2Gi'
        }
    }

    s.k8s_res_limits_cpu        = limits_cpu
    s.k8s_res_limits_memory     = limits_memory 
    s.k8s_res_requests_cpu      = requests_cpu
    s.k8s_res_requests_memory   = requests_memory 

    Config.data                 = s + args
}

def defaultSettings(){
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
        base_company                    : metis.companyName(),
        base_dir                        : pwd(),
        base_env                        : ENVIRONMENT.toLowerCase(),
        base_envs                       : [],
        base_name                       : metis.appName(),
        base_port                       : 8080,
        base_project                    : metis.projectName(),
        base_web_root                   : '/data/app/',
        base_workspace                  : JENKINS_HOME + '/workspace/' + JOB_NAME.toLowerCase(),
        build_command                   : 'mvn',
        build_dir                       : '.',
        build_language                  : 'java',
        build_language_version          : 0,
        build_legacy                    : false,
        build_node_name                 : '',
        build_only                      : false,
        build_options                   : '',
        build_stage                     : 'master',
        build_user                      : metis.buildUserName(),
        build_userid                    : metis.buildUserNameID(),
        check_image_exist               : false,
        check_permission_by_gitlab      : false,
        docker_account                  : 'Registry-Jenkins',
        docker_file                     : 'Dockerfile.jenkins',
        docker_img_name                 : '',
        docker_img_tag                  : '',
        docker_img_build_timeout        : 1800,
        docker_img_build_options        : '',
        docker_img_push_timeout         : 600,
        docker_login_timeout            : 15,
        docker_registry                 : 'registry.rulin.io',
        docker_registry_basic_auth      : 'Jenkins-Basic-Auth-2-Registry',
        docker_templates_resources      : 'io/rulin/templates/',
        docker_templates_local          : '/data/jenkins/projects/',
        git_commit_length               : 8,
        git_commit_id                   : '',
        git_credentials_api_token       : 'gitlab-api-token',
        git_credentials_clone           : 'GitLab-Jenkins-UP',
        git_hosts                       : 'https://git.rulin.io',
        git_repo                        : null,
        git_repo_id                     : 0,
        git_revision                    : GIT_REVISION,
        git_stage                       : 'master',
        git_timeout_seconds             : 1800,
        hosted_by                       : 'container',
        k8s_allowed_commands            : ['apply', 'create', 'delete', 'get', 'autoscale'],
        k8s_config_dir                  : '/home/jenkins/.kube/',
        k8s_dashboard_url               : metis.k8sDashboardURL(),
        k8s_hc_liveness_type            : metis.defaultHealthCheckType('live'),
        k8s_hc_liveness_path            : '/',
        k8s_hc_liveness_port            : 8080,
        k8s_hc_liveness_ids             : 60,
        k8s_hc_liveness_timeout         : 5,
        k8s_hc_readiness_ids            : null,
        k8s_hc_readiness_timeout        : null,
        k8s_hc_readiness_type           : '',
        k8s_hc_readiness_path           : '',
        k8s_hc_readiness_path           : null,
        k8s_img_pull_policy             : 'Always',
        k8s_img_pull_secret             : 'images-puller-' + metis.companyName() + '-' + metis.projectName(),
        k8s_min_ready_seconds           : 60,
        k8s_namespace                   : metis.k8sNamespace(),
        k8s_progress_deadline_sec       : 300,
        k8s_replicas                    : metis.k8sReplicas(),
        k8s_rev_history_limit           : 10,
        k8s_strategy_max_surge          : '25%',
        k8s_strategy_max_unavailable    : '25%',
        k8s_termination_GPS             : 60,
        k8s_yml_default_dir             : 'kubernetes/yaml/default/',
        k8s_yml_default_deploy          : 'deployment.yaml',
        k8s_yml_default_svc             : 'service.yaml',
        notice_always                   : false,
        notice_timeout                  : 15,
        run_command                     : '',
        run_user                        : 1000,
        skip_build                      : false,
        skip_docker                     : false,
        skip_deploy                     : false,
        skip_git                        : false,
        skip_k8s                        : false,
        skip_sonar                      : false,
        skip_test                       : true,
        sonar_scanner                   : '/data/jenkins/tools/sonar-scanner/bin/sonar-scanner',
        sonar_options                   : '',
        ssh_host                        : null,
        ssh_speed_limit                 : 20480,
        ssh_user                        : 'www',
        ssh_port                        : 22,
        stage_docker                    : 'master',
        stage_git                       : 'master',
        stage_sonar                     : 'master',
        stage_k8s                       : 'master',
        stage_pre_process               : 'master',
        stage_build                     : '',
        test_command                    : 'mvn',
        test_options                    : 'test',
        test_type                       : 'unit'
    ]

    return Config.settings
}

return this