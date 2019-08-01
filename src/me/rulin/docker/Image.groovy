/* Image.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.docker

private String cmd(String c){
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

private build(String build_args) {
    check.file('Dockerfile')

    try {
        log.info "Build image: " + image

        timeout(time: DOCKER_IMAGE_BUILD_TIMEOUT, unit: 'SECONDS') {
            cmd("build $DOCKER_IMAGE_BUILD_OPTIONS -t $image .")
        }
    }
    catch (e) {
        println "Error occurred during build image"
        throw e
    }
}

def version(){
    cmd("version")
}

def images(){
    cmd("images")
}

def push(){
    cmd("push ")
}

def login(){
    cmd("login")
}

def logout(){
    cmd("logout")
}