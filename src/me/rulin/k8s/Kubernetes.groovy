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

        println data

        println data.metadata.name
        println data.spec.template.spec.containers[0]
        println data.spec.template.spec.containers[0].image

        //writeYaml file: yaml_file, data: yaml_data, overwrite: true
    }
    catch (e) {
        throw e
    }
}