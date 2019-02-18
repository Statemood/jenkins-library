/* Dockerfile.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def dockerfileGenerate() {
    dockerfileCopyTemplate()

    // Test Dockerfile exist
    check.file('Dockerfile')
}

def dockerfileCopyTemplate(list) {
    // Remove old files first
    try {
        if (fileExists(DOCKERFILES)) {
            log.notice "Removing " + DOCKERFILES
            sh("rm -rf $DOCKERFILES")
        }
    } 
    catch (e) {
        throw e
    }

    // Process dockerignore file
    if (fileExists(DOCKERIGNORE_FILE)) {
        log.notice "Copy dockerignore file"

        sh("cp -rf $DOCKERIGNORE_FILE .")
    } else {
        log.warning "No $DOCKERIGNORE_FILE found, ignored"
    }

    try {
        for (d in list) {
            if (fileExists(d)) {
                log.notice "Use Dockerfiles $d" 
                sh("cp -frH $d/* .")

                return
            }
        }
    } 
    catch (e) {
        throw e
    }
}

def imageBuild(image) {
    check.file('Dockerfile')

    try {
        log.info "Build image: " + image

        timeout(time: DOCKER_IMAGE_BUILD_TIMEOUT, unit: 'SECONDS') {
            sh("sudo docker build $DOCKER_IMAGE_BUILD_OPTIONS -t $image .")
        }
    }
    catch (e) {
        println "Error occurred during build image"
        throw e
    }
}

def imagePush(image) {
    try {
        log.info "Push image " + image

        timeout(time: DOCKER_IMAGE_PUSH_TIMEOUT, unit: 'SECONDS') {
            sh("sudo docker push $image")
        }
    }
    catch (e) {
        println "Error occurred during push image"
        throw e
    }
}

def registryLogin(){
    return 
}

def registryLogout(){
    return 
}