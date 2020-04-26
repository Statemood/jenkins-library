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
    def private dfc = []
    
    def private cid = git.commitID()

    dfc.add("LABEL made.by=Jenkins job.name=$JOB_NAME build.user=$BUILD_USER build.number=$BUILD_NUMBER")
    dfc.add("LABEL git.commit.id=$cid")
    dfc.add("RUN mkdir -p $d")
    dfc.add("COPY $t $d")

    sh("echo >> $f")

    for(s in dfc) {
        sh("echo $s >> $f")
    }
}