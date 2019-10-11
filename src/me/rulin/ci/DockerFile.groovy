/* File.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def generate(String f='Dockerfile', String t='.', String d='/data/app', String e=null) {
    copyTemplate()

    // Test Dockerfile exist
    check.file(f)
    sh("echo RUN mkdir -p $d >> $f")
    sh("echo COPY $t $d      >> $f")
}

def copyTemplate(list) {
    // Remove old files first
    try {
        if (fileExists(DOCKERFILES)) {
            log.i "Removing " + DOCKERFILES
            sh("rm -rf $DOCKERFILES")
        }
    } 
    catch (e) {
        throw e
    }

    // Process dockerignore file
    if (fileExists(DOCKERIGNORE_FILE)) {
        log.i "Copy dockerignore file"

        sh("cp -rf $DOCKERIGNORE_FILE .")
    } 
    else {
        log.w "File not found: $DOCKERIGNORE_FILE, ignored"
    }

    try {
        for (d in list) {
            if (fileExists(d)) {
                log.i "Use Dockerfiles $d" 
                sh("cp -frH $d/* .")

                return
            }
        }
    } 
    catch (e) {
        throw e
    }
}

def registryLogin(){
    return 
}

def registryLogout(){
    return 
}