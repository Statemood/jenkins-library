/* Kubernetes.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// Usage: 
//      k8s.command("create", "deployment")

package me.rulin.k8s

def generateYaml(String yaml_file="${APP_NAME}.yaml"){
    try {
        def private yml = readYaml file: yaml_file
        def private   s = yml.spec
        def private   c = s.template.spec.containers[0]
        def private res = c.resources

        println yml
        
        yml.apiVersion                  = "apps/v1"
        yml.kind                        = "Deployment"
        yml.metadata.name               = APP_NAME
        s.replicas                      = K8S_REPLICAS
        s.selector.matchLabels.app      = APP_NAME
        s.template.metadata.labels.app  = APP_NAME
        
        c.name                  = APP_NAME
        c.image                 = DOCKER_IMAGE
        c.imagePullPolicy       = "Always"
        c.imagePullSecret       = "image-pull-secret-" + PROJECT_NAME
        res.requests.cpu        = K8S_REQUESTS_CPU
        res.requests.memory     = K8S_REQUESTS_MEMORY
        res.limits.cpu          = K8S_LIMITS_CPU
        res.limits.memory       = K8S_LIMITS_MEMORY

        println yml

        writeYaml file: yaml_file, data: yml, overwrite: true
    }
    catch (e) {
        throw e
    }
}