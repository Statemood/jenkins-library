/* Controller.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

import me.rulin.ci.Git

def controller (){
    log.info "Git clone code"
    Git.checkout(repo, SCM_REVISION)

}