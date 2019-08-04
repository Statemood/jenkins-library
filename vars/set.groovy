/* set.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def call(Map args = [:]) {
    Config.data  += args

    stages.loadSettings()
    stages.preProcess()
    stages.gitClone()
    stages.compile()
    stages.testJunit()
}
 
return this