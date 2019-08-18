/* set.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def call(Map args = [:]) {

    Config.data['repo']         = args.containsKey('repo')        ?: null
    Config.data['revision']     = args.containsKey('revision')    ?: GIT_REVISION
    Config.data['lang']         = args.containsKey('lang')        ?: "java"

    Config.data += args 

    println Config.data 
    println args 

    stagesController.loadSettings()
    stagesController.preProcess()
    stagesController.gitClone()
    stagesController.compile()
    stagesController.testJunit()
}
 
return this