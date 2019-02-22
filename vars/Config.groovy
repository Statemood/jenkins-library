/* CoConfig.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// CI Config
class Config implements Serializable {
    Map args = [:]

    Map data = [:]

    def merge(Map d) {
        static Map data = d
    }
}