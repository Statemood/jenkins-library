/* DockerFile.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def generate(String f='Dockerfile', String t='.', String d='/data/app', String e=null){
    if (fileExists(DOCKERIGNORE_FILE)) {
        log.i "Copy dockerignore file"

        sh("cp -rf $DOCKERIGNORE_FILE .")
    } 
    else {
        log.w "File not found: $DOCKERIGNORE_FILE, ignored"
    }
    // Test Dockerfile exist
    check.file(f)
    sh("echo RUN mkdir -p $d >> $f")
    sh("echo COPY $t $d      >> $f")
}

def registryLogin(){
    return 
}

def registryLogout(){
    return 
}