/* Language.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.03 23:30          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def build(String b_file=null){

    log.i 'Preparing to build ' + Config.data.build_language.toUpperCase() + ' project.'

    private  bc = Config.data.build_command
    private  bo = Config.data.build_options
    private cmd = bc + ' ' + bo

    try {
        if (b_file){
            check.file(b_file)
                    
            log.i 'Build with command: ' + bc + ', options: ' + bo
            sh(cmd)
        }
    }
    catch (e) {
        throw e
    }
}