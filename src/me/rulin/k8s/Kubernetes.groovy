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

def updateYaml(String yaml_file="k8s.yaml"){
    try {
        def private data = readYaml file: yaml_file
        def c   = data.spec.template.spec.containers[0]
        def res = c.resources
        println data

        /*
        data.metadata.name  = APP_NAME
        c.image             = DOCKER_IMAGE
        c.imagePullPolicy   = "Always"
        res.requests.cpu    = K8S_REQUESTS_CPU
        res.requests.memory = K8S_REQUESTS_MEMORY
        res.limits.cpu      = K8S_LIMITS_CPU
        res.limits.memory   = K8S_LIMITS_MEMORY
        */

        data.replaceAll("APP_NAME", APP_NAME)

        println data

        //writeYaml file: yaml_file, data: yaml_data, overwrite: true
    }
    catch (e) {
        throw e
    }
}