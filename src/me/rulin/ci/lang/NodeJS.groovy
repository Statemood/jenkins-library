/* NodeJS.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.03 23:50          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci.lang

import  me.rulin.ci.lang.Language

def npmBuild(){
    mb = new Language()
    mb.executor('package.json')
}