/* set.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def call(Map args = [:]) {
    /*
    Order:
        1. Local Settings
        2. .jenkins.yaml
        3. Config
    */

    stagesController.loadSettings()

    if(!ACTION) { ACTION = "deploy" }

    Config.data['repo']             = args.containsKey('repo')              ?: null
    Config.data['revision']         = args.containsKey('revision')          ?: GIT_REVISION
    Config.data['language']         = args.containsKey('language')          ?: "java"
    Config.data['action']           = ACTION
    Config.data['build.user']       = BUILD_USER
    Config.data['env']              = ENVIRONMENT
    Config.data['credentials.id']   = args.containsKey('credentials.id')    ?: "DefaultGitSCMCredentialsID"

    Config.data += args 

    println Config.data 
    println args 

    buildInfo()
    stagesController.preProcess()
    stagesController.gitClone()
    stagesController.compile()
    stagesController.testJunit()
    stagesController.dockerStage()
}

// Set build info
def buildInfo(){
    currentBuild.displayName = BUILD_NUMBER + "-" + Config.data['env']
    currentBuild.description = Config.data['action'] + " by user " + Config.data['build.user'] + ", version " + Config.data['revision'] 
}

return this