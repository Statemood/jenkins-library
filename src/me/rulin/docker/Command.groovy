/* Command.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.03 23:30          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.docker

def String command(String c){
    try {
        sh("sudo docker $c")
    }
    catch (e) {
        throw e
    }
}