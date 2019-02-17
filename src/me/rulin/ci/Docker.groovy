/* Docker.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci


class Docker implements Serializable {
    public static final String DOCKER_NAME = 'default docker name'

}

class Version {
    sh("sudo docekr version")
}
/*
def Images(){
    echo "List all docker images"
    sh("sudo docker images")
}

return this*/