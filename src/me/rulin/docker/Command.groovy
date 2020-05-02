/* Command.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.03 23:30          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.docker

class Command implements Serializable {
    def String cmd(String c){
        if(c) {
            try {
                sh("sudo docker $c")
            }
            catch (e) {
                throw e
            }
        }
        else {
            error "No docker command input"
        }
    }
}