/* Yaml.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.kubernetes

def locationKind(String kd, String f) {
    try {
        def private    yml = readYaml file: f

        if (yml.size() > 0 && yml[0] != null) {
            for (int i=0; i<yml.size(); i++) {
                if (yml[i].kind == kd) {
                    return "[$i]"
                }
            }
        }
        else {
            return 
        }
    } 
    catch (e) {
        throw e
    }
}

def deployment(String f){
    try {
        def private yml = readYaml file: f
        def private  lt = locationKind("Deployment", f)
        def private   s = yml[lt].spec
        def private  md = yml[lt].metadata

        println lt
        println s 
        println md 
        def private   c = s.template.spec.containers[0]

        println c
        println c.resources 
        def private res = c.resources
        def private ssr = s.strategy.rollingUpdate
        def private ips = c.imagePullSecret

        md.name                         = APP_NAME
        md.namespace                    = K8S_NAMESPACE

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
        c.imagePullPolicy               = "Always"

        if (ips) {
            ips[0].name                 = "image-pull-secret-" + PROJECT_NAME
        }

        res.requests.cpu        = K8S_REQUESTS_CPU
        res.requests.memory     = K8S_REQUESTS_MEMORY
        res.limits.cpu          = K8S_LIMITS_CPU
        res.limits.memory       = K8S_LIMITS_MEMORY

        writeYaml file: yaml_file, data: yml, overwrite: true
    }
    catch (e) {
        throw e
    }
}

def service(String f){
    try {
        def private      yml = readYaml file: f
        def private location = locationKind("Service", f)
        def private      svc = yml[location]
        def private       md = svc.metadata
        def private        s = svc.spec

        md.name              = APP_NAME
        md.namespace         = K8S_NAMESPACE
        s.selector.app       = APP_NAME
        s.ports[0].name      = "http"
        s.ports[0].port      = APP_PORT

        writeYaml file: yaml_file, data: yml, overwrite: true
    }
    catch (e) {
        log.e "Oops! An error occurred during update serivce, file: " + f
        throw e
    }
}