def call(Map args = [:]) {
    /*
    Order:
        1. Local Settings
        2. .jenkins.yaml
        3. Config
    */

    loadSettings()

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

    log.i "Using workspace: " + FIRST_DIR

    stagesController.stagePreProcess()
    stagesController.stageGitClone()
    stagesController.stageBuild()
    stagesController.stageTest()
    stagesController.stageDocker()
    stagesController.stageKubernetes()
}