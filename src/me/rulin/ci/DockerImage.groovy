/* DockerImage.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def private String cmd(String c){
    if(c) {
        try {
            sh("sudo docker $c")
        }
        catch (e) {
            throw e
        }
    }
    else {
        error "No docker cmd input"
    }
}

def private build(String image_name) {
    check.file('Dockerfile')
    try {
        
        log.info "Build image: " + image_name

        timeout(time: DOCKER_IMAGE_BUILD_TIMEOUT, unit: 'SECONDS') {
            cmd("build $DOCKER_IMAGE_BUILD_OPTIONS -t $image_name .")
        }
    }
    catch (e) {
        println "Error occurred during build image"
        throw e
    }
}

def private version(){
    cmd("version")
}

def private images(){
    cmd("images")
}

def private push(String image_name){
    try {
        log.info "Push image " + image_name

        timeout(time: DOCKER_IMAGE_PUSH_TIMEOUT, unit: 'SECONDS') {
            cmd("push $image_name")
        }
    }
    catch (e) {
        println "Error occurred during push image"
        throw e
    }
}

def login(String reg=DOCKER_REGISTRY, String opt=null){
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
                cmd("login -u $registry_username -p $registry_password $reg")
            }
        }
    }
    catch (e) {
        println "Error occurred during push image"
        throw e
    }
}

def logout(){
    cmd("logout")
}