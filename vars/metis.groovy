def getApplicationName(){
    return JOB_BASE_NAME.split('_')[1].toLowerCase()
}

def getBuildUserName(){
    /* Get User info
       Requeire 'build user vars' plugin
       See https://plugins.jenkins.io/build-user-vars-plugin for more information
    */

    wrap([$class: 'BuildUser']) { 
        return BUILD_USER 
    }
}

def getBuildUserNameID(){
    wrap([$class: 'BuildUser']) { 
        return BUILD_USER_ID 
    }
}

def getFirstDirectory(){
    return pwd()
}

def getFrojectName(){
    return JOB_BASE_NAME.split('_')[0].toLowerCase()
}

def getGetCommitID(){
    return Config.data.git_commit_id = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
}

def getReplicasNumber(){
    Integer repn = 2

    def dq = ['dev', 'test', 'qa']
    if (ENVIRONMENT in dq) {
        repn =  1
    }

    return repn
}