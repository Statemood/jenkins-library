/* DockerFile.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def private generate(String f='Dockerfile', String t='.', String d='/data/app', String c=null){
    if (fileExists(DOCKERIGNORE_FILE)) {
        log.i "Copy dockerignore file"

        sh("cp -rf $DOCKERIGNORE_FILE .")
    } 
    else {
        log.w "File not found: $DOCKERIGNORE_FILE, ignored"
    }
    // Test Dockerfile exist
    check.file(f)

    def image_labels  = "Created=Jenkins JobName=$JOB_NAME BuildUser=$BUILD_USER "
    def image_labels += "BuildNumber=$BUILD_NUMBER"

    sh("echo LABEL $image_labels    >> $f")
    sh("echo RUN mkdir -p $d        >> $f")
    sh("echo COPY $t $d             >> $f")
}