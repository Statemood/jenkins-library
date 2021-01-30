/* Yaml.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.kubernetes

def deployment(String f){
    try {
        if (f) {
            def private yml = readYaml file: f
        }
        else {
            def private yml = readYaml text: libraryResource('me/rulin/templates/k8s/yaml/deployment.yaml')
        }
    
        def private        s = yml.spec
        def private       md = yml.metadata
        def private        c = s.template.spec.containers[0]
        def private      res = c.resources
        def private      ssr = s.strategy.rollingUpdate
        def private      ips = c.imagePullSecret
        def private        e = c.env

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

        sh("mkdir -pv k8s")
        writeYaml file: "k8s/deployment.yaml", data: yml, overwrite: true
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
        s.ports[0].port      = APP_PORT.toInteger()

        writeYaml file: f, data: yml, overwrite: true
    }
    catch (e) {
        log.e "Oops! An error occurred during update serivce, file: " + f
        throw e
    }
}