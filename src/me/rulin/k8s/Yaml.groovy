/* Yaml.groovy
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

def k8sService(Map ky){
    ky.apiVersion = "v1"
    ky.kind = "Service"
}

def updateYaml(String yaml_file, Map yaml_data){
    try {
        writeYaml file: yaml_file, data: yaml_data, overwrite: true
    }
    catch (e) {
        throw e
    }
}