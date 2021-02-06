/* Yaml.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.kubernetes

def yamlReader(String t, String f=null) {
    try {
        if (t == null) {
            log.err "Missing arguments. yamlReader require at lease on argument."
        }
        if (f) {
            check.file(f)

            log.i "Read yaml from file: " + f

            def    y = readYaml file: f
            return y
        }
        else {
            log.i "Read yaml from template file: " + t 

            String txt = libraryResource(t)
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
            Config.data.k8s_yml_default_templates_dir + 
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
        def private        c = s.template.spec.containers[0]
        def private      res = c.resources
        def private      ssr = s.strategy.rollingUpdate
        def private      ips = c.imagePullSecret
        def private        e = c.env

        md.name                              = cfg.base_name
        md.namespace                         = cfg.k8s_namespace
        md.labels.app                        = cfg.base_name

        s.replicas                           = cfg.k8s_replicas
        s.selector.matchLabels.app           = cfg.base_name
        s.template.metadata.labels.app       = cfg.base_name
        s.spec.terminationGracePeriodSeconds = cfg.k8s_termination_GPS.toInteger()
        s.revisionHistoryLimit               = cfg.k8s_rev_history_limit.toInteger()
        s.progressDeadlineSeconds            = cfg.k8s_progress_deadline_sec.toInteger()
        ssr.maxSurge                         = cfg.k8s_strategy_max_surge
        ssr.maxUnavailable                   = cfg.k8s_strategy_max_unavailable

        c.name                               = cfg.base_name
        c.image                              = cfg.docker_img_name
        c.imagePullPolicy                    = cfg.k8s_img_pull_policy

        if (ips) {
            ips[0].name                      = cfg.k8s_img_pull_secret
        }

        if (e) {
            if(e.size() > 0){
                int en = 0
                e[en].name              = "ENVIRONMENT"
                e[en].value             = ENVIRONMENT
                //do for
            }
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
        Config.data.k8s_yml_default_templates_dir + 
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
        s.ports[0].name      = "http"
        s.ports[0].port      = cfg.base_port

        def cmd_create_dir = 'mkdir -pv ' + cfg.k8s_yml_default_dir
        sh(cmd_create_dir)

        writeYaml file: dest_file, data: yml, overwrite: true
    }
    catch (e) {
        log.e "Oops! An error occurred during update serivce, file: " + dest_file
        throw e
    }
}