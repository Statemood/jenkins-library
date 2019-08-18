/* File.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.docker

def dockerfileGenerate(String f='Dockerfile') {
    dockerfileCopyTemplate()

    // Test Dockerfile exist
    check.file(f)
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

}

def imagePush(image) {

}

def registryLogin(){
    return 
}

def registryLogout(){
    return 
}