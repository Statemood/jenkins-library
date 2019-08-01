/* Java.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.01 21:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci.lang

def initBuild(String b_file){
    log.i "Preparing Java build"

    private  bc = Config.data['build_command']
    private  bo = Config.data['build_options']
    private cmd = bc + bo

    try {
        if (fileExists(b_file)){
            log.i "Build with command: " + bc + ", options: " + bo
            sh(cmd)
        }
        else {
            log.e "File not found: '" + b_file + "', aborted"
        }
    }
    catch (e) {
        throw e
    }
}

def mavenBuild(){
    initBuild('pom.xml')
}

def gradleBuild(){
    initBuild('build.gradle')
}