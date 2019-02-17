/* Docker.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci


class Docker {
    public static final String DOCKER_NAME = 'default docker name'

    def images(){
        try {
            sh "sudo docker images"
        }
        catch (e) {
            throw e
        }
        return true
    }
}

/*
String images() {
    sh "sudo docker images"
}

return this*/