/* Image.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.docker

import  me.rulin.docker.Command

def private build(String image_name) {
    def cmd = new Command()

    check.file('Dockerfile')
    try {
        log.info "Build image: " + image_name

        timeout(time: DOCKER_IMAGE_BUILD_TIMEOUT, unit: 'SECONDS') {
            //String sc = "build $DOCKER_IMAGE_BUILD_OPTIONS -t $image_name .".
            cmd.command("version")
        }
    }
    catch (e) {
        println "Error occurred during build image"
        throw e
    }
}

def private images(){
    cmd.exec("images")
}

def private push(String image_name){
    def cmd = new Command()
    try {
        log.info "Push image " + image_name

        timeout(time: DOCKER_IMAGE_PUSH_TIMEOUT, unit: 'SECONDS') {
            cmd.exec("push $image_name")
        }
    }
    catch (e) {
        println "Error occurred during push image"
        throw e
    }
}

def login(String reg=DOCKER_REGISTRY, String opt=null){
    def cmd = new Command()
    if(reg){
        private r = ""
    }
    try {
        log.i "Login to Docker Registry " + reg

        timeout(time: DOCKER_IMAGE_PUSH_TIMEOUT, unit: 'SECONDS') {
            withCredentials([
                usernamePassword(
                    credentialsId: 'Docker-Registry',
                    passwordVariable: 'registry_password',
                    usernameVariable: 'registry_username'
                )
            ]) {
                cmd.exec("login -u $registry_username -p $registry_password $reg")
            }
        }
    }
    catch (e) {
        println "Error occurred during push image"
        throw e
    }
}

def logout(){
    cmd.exec("logout")
}