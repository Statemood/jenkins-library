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
    stagesController.test()
    stagesController.docker()
    stagesController.kubernetes()
}

// Set build info
def buildInfo(){
    def private name = BUILD_NUMBER + "-" + Config.data['env']
    def private desc = Config.data['action'] + " by " + Config.data['build.user'] + ", version " + Config.data['revision'] 

    currentBuild.displayName = name 
    currentBuild.description = desc 
}

return this