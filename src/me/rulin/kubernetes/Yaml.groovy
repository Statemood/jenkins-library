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
        def private      yml = yamlReader(f)
        def private        s = yml.spec
        def private       md = yml.metadata
        def private        c = s.template.spec.containers[0]
        def private      res = c.resources
        def private      ssr = s.strategy.rollingUpdate
        def private      ips = c.imagePullSecret
        def private        e = c.env

        md.name                         = APP_NAME
        md.namespace                    = K8S_NAMESPACE
        md.labels.app                   = APP_NAME

        s.replicas                      = K8S_REPLICAS.toInteger()
        s.selector.matchLabels.app      = APP_NAME
        s.template.metadata.labels.app  = APP_NAME
        s.terminationGracePeriodSeconds = K8S_GRACE_PERIOD_SECONDS.toInteger()
        s.revisionHistoryLimit          = K8S_REVISION_HISTORY_LIMIT.toInteger()
        s.progressDeadlineSeconds       = K8S_PROGRESS_DEADLINE_SECONDS.toInteger()
        ssr.maxSurge                    = K8S_STRATEGY_MAX_SURGE
        ssr.maxUnavailable              = K8S_STRATEGY_MAX_UNAVAILABLE

        c.name                          = APP_NAME
        c.image                         = DOCKER_IMAGE
        c.imagePullPolicy               = K8S_IMAGE_PULL_POLICY

        if (ips) {
            ips[0].name                 = "image-pull-secret-" + PROJECT_NAME
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