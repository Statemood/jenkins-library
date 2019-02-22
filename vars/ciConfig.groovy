/* ciConfig.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

class ciConfig implements Serializable {
    // Local Settings
    Map config   = [:]

    // Job input parameters
    static Map data     = [:]

    def call() {
        log.info "Setting data"

        static Map data = config + data
    }
}