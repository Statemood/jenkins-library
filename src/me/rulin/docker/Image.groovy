/* Image.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// Docker image Build & Push

package me.rulin.docker

private def build(image) {
    check.file('Dockerfile')

    try {
        log.info "Build image: " + image

        timeout(time: DOCKER_IMAGE_BP_TIMEOUT, unit: 'SECONDS') {
            sh("sudo docker build $DOCKER_IMAGE_BUILD_OPTIONS -t $image .")
        }
    }
    catch (e) {
        println "Error occurred during build image"
        error e
    }
}

def push(image) {
    try {
        log.info "Push image " + image

        timeout(time: DOCKER_IMAGE_BP_TIMEOUT, unit: 'SECONDS') {
            sh("sudo docker push $image")
        }
    }
    catch (e) {
        println "Error occurred during push image"
        error e
    }
}