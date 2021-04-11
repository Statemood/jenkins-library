/* Yaml.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.kubernetes

def yamlReader(String t, String f=null){
    // 使用 libraryResouce 方法读取 Yaml 文件
    try {
        if (f != null) {
            check.file(f)

            log.i "Read yaml from file: " + f

            def    y = readYaml file: f
            return y
        }
        else {
            log.i "Read yaml from template file: " + t 

            def  txt = libraryResource(t)
            def    y = readYaml text: txt
            return y
        }
    }
    catch (e) {
        throw e
    }
}

def deployment(
        String source_file = 
            Config.data.docker_templates_resources + 
            Config.data.k8s_yml_default_dir + 
            Config.data.k8s_yml_default_deploy, 
        String dest_file =
            Config.data.k8s_yml_default_dir + 
            Config.data.k8s_yml_default_deploy
    ){
    try {
        def private      cfg = Config.data
        def private      yml = yamlReader(source_file)
        def private        s = yml.spec
        def private       md = yml.metadata
        def private       ts = s.template.spec
        def private        c = ts.containers[0]
        def private      res = c.resources
        def private      ssr = s.strategy.rollingUpdate
        def private      ips = ts.imagePullSecrets

        md.name                             = cfg.base_name
        md.namespace                        = cfg.k8s_namespace
        md.labels.app                       = cfg.base_name

        s.replicas                          = cfg.k8s_replicas
        s.selector.matchLabels.app          = cfg.base_name
        s.template.metadata.labels.app      = cfg.base_name
        ts.terminationGracePeriodSeconds    = cfg.k8s_termination_GPS.toInteger()
        s.revisionHistoryLimit              = cfg.k8s_rev_history_limit.toInteger()
        s.progressDeadlineSeconds           = cfg.k8s_progress_deadline_sec.toInteger()
        ssr.maxSurge                        = cfg.k8s_strategy_max_surge
        ssr.maxUnavailable                  = cfg.k8s_strategy_max_unavailable

        c.name                              = cfg.base_name
        c.image                             = cfg.docker_img_name + ':' + cfg.docker_img_tag
        c.imagePullPolicy                   = cfg.k8s_img_pull_policy
        
        ips[0].name                         = cfg.k8s_img_pull_secret

        /*
        if (e) {
            if(e.size() > 0){
                int en = 0
                e[en].name              = 'SPRING_PROFILES_ACTIVE'
                e[en].value             = ENVIRONMENT.toLowerCase()
                //do for
            }
        }*/

        private found_environment = false
        c.env.each{
            if(it.name == 'ENVIRONMENT'){
                it.value          = ENVIRONMENT
                found_environment = true
                return true
            }
        }

        if(!found_environment){ c.env.add(['name': 'ENVIRONMENT', 'value': ENVIRONMENT]) }

        c.env.add(['name': 'NAMESPACE', 'value': cfg.k8s_namespace])
        c.env.add(['name': 'APP_NAME', 'value': cfg.base_name])

        if(cfg.base_envs.size() > 0){
            cfg.base_envs.each{
                c.env.add(it)
            }
        }

        // 判断 base_port 是否大于一条，是则进入循环并替换 'port' -> 'containerPort'
        if(cfg.base_port.size() > 0){
            log.i 'Use ports: ' + cfg.base_port
            private List ports = []

            cfg.base_port.each{
                private Map     port        = [:]
                cfg.k8s_hc_liveness_port    = it.port
                port.name                   = it.name 
                port.containerPort          = it.port
                if(it.containsKey('protocol')){ 
                    port.protocol           = it.protocol 
                }
                ports.add(port)
            }
            c.ports = ports
        }
        
        // health check
        if(cfg.k8s_hc_liveness_type == 'httpGet'){
            def private  live =  c.livenessProbe

            live.httpGet.port           = cfg.k8s_hc_liveness_port
            live.httpGet.path           = cfg.k8s_hc_liveness_path
            live.initialDelaySeconds    = cfg.k8s_hc_liveness_ids
            live.timeoutSeconds         = cfg.k8s_hc_liveness_timeout
        }

        if(cfg.k8s_hc_readiness_type == 'httpGet'){
            def private ready = c.readinessProbe
            ready.httpGet.port          = cfg.k8s_hc_readiness_port     ?: cfg.k8s_hc_liveness_port
            ready.httpGet.path          = cfg.k8s_hc_readiness_path     ?: cfg.k8s_hc_liveness_path
            ready.initialDelaySeconds   = cfg.k8s_hc_readiness_ids      ?: cfg.k8s_hc_liveness_ids
            ready.timeoutSeconds        = cfg.k8s_hc_readiness_timeout  ?: fg.k8s_hc_liveness_timeout
        }

        res.requests.cpu        = cfg.k8s_res_requests_cpu
        res.requests.memory     = cfg.k8s_res_requests_memory
        res.limits.cpu          = cfg.k8s_res_limits_cpu
        res.limits.memory       = cfg.k8s_res_limits_memory

        def cmd_create_dir = 'mkdir -pv ' + cfg.k8s_yml_default_dir
        sh(cmd_create_dir)
        writeYaml file: dest_file, data: yml, overwrite: true
    }
    catch (e) {
        throw e
    }
}

def service(
    String source_file = 
        Config.data.docker_templates_resources + 
        Config.data.k8s_yml_default_dir + 
        Config.data.k8s_yml_default_svc, 
    String dest_file =
        Config.data.k8s_yml_default_dir + 
        Config.data.k8s_yml_default_svc
){
    try {
        def private      yml = yamlReader(source_file)
        def private       md = yml.metadata
        def private        s = yml.spec
        def private      cfg = Config.data

        md.name              = cfg.base_name
        md.namespace         = cfg.k8s_namespace
        s.selector.app       = cfg.base_name

        if(cfg.base_port.size() > 0){
            log.i 'Use ports: ' + cfg.base_port
            s.ports = cfg.base_port
        }

        def cmd_create_dir = 'mkdir -pv ' + cfg.k8s_yml_default_dir
        sh(cmd_create_dir)

        writeYaml file: dest_file, data: yml, overwrite: true
    }
    catch (e) {
        log.e 'Oops! An error occurred during update serivce, file: ' + dest_file
        throw e
    }
}