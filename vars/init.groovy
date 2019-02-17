/* init.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import me.rulin.ci.Docker
import me.rulin.ci.Settings

echo "Call init"

def call(body){
    dockerCmd   = new Docker()

    echo "Workspace is : " Settings.WS

    dockerCmd.images()
    dockerCmd.version()
}