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

def deployment(String f=null){
    try {
        def private      cfg = Config.data
        def private      yml = yamlReader(f)
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

        s.replicas                           = K8S_REPLICAS.toInteger()
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

        res.requests.cpu        = K8S_REQUESTS_CPU
        res.requests.memory     = K8S_REQUESTS_MEMORY
        res.limits.cpu          = K8S_LIMITS_CPU
        res.limits.memory       = K8S_LIMITS_MEMORY

        sh("mkdir -pv k8s")
        writeYaml file: "k8s/deployment.yaml", data: yml, overwrite: true
    }
    catch (e) {
        throw e
    }
}

def service(String f=null){
    try {
        def private      yml = yamlReader(f)
        def private       md = yml.metadata
        def private        s = yml.spec

        md.name              = APP_NAME
        md.namespace         = K8S_NAMESPACE
        s.selector.app       = APP_NAME
        s.ports[0].name      = "http"
        s.ports[0].port      = APP_PORT.toInteger()

        writeYaml file: 'k8s/service.yaml', data: yml, overwrite: true
    }
    catch (e) {
        log.e "Oops! An error occurred during update serivce, file: " + f
        throw e
    }
}