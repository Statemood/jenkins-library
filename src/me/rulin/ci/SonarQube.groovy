/* SonarQuebScanner.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

import  me.rulin.ci.Git

def scan() {
    try {
        log.info "Preparing SonarQube Scanner"

        generateProperties()

    

    }
    catch (e) {
        throw e
    }
}

def generateProperties(){
    private sppf = "sonar-project.properties"

    if (fileExists(sppf)) {
        log.info "Found and use $sppf"
    }
    else {
        private gsid = Git.commitIDShort()

        log.info "Generate $sppf"

        sh("echo \"sonar.projectKey=$JOB_NAME\"      > $sppf")
        sh("echo \"sonar.projectName=$JOB_NAME\"    >> $sppf")
        sh("echo \"sonar.projectVersion=$gsid\"     >> $sppf")
        sh("echo \"sonar.sources=.\"                >> $sppf")
    }
}