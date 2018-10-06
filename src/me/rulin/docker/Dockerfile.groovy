/* Dockerfile.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                               #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.docker

def generate() {
    copyTemplate(DOCKERFILE_TEMPLATE_LIST)

    // Test Dockerfile exist
    check.file('Dockerfile')


}

private def copyTemplate(list) {
    // Remove old files first
    try {
        if (fileExists(DOCKERFILES)) {
            log.notice "Removing " + DOCKERFILES
            sh("rm -rf $DOCKERFILES")
        }
    } catch (e) {
        log.error e
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
    } catch (e) {
        log.error e
    }
}