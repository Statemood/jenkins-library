/* Java.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.01 21:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci.lang

import  me.rulin.ci.lang.Language

def mavenBuild(){
    mb = new Language()

    mb.executor('pom.xml')
}

def gradleBuild(){
    gb = new Language()
    gb.executor('build.gradle')
}

def antBuild(){
    return
}