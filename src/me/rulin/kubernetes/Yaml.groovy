/* Yaml.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.kubernetes

def generate(String yaml_file="k8s.yaml"){
    try {
        def private yml = readYaml file: yaml_file
        def private   s = yml.spec
        def private  md = yml.metadata
        def private   c = s.template.spec.containers[0]
        def private res = c.resources
        def private ssr = s.strategy.rollingUpdate

        yml.apiVersion                  = "apps/v1"
        yml.kind                        = "Deployment"
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
        c.imagePullSecret[0].name       = "image-pull-secret-" + PROJECT_NAME
        

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