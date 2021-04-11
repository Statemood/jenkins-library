import io.rulin.ci.Git

def appName(){
    return JOB_BASE_NAME.split('_')[2].toLowerCase()
}

def projectName(){
    return JOB_BASE_NAME.split('_')[1].toLowerCase()
}

def companyName(){
    return JOB_BASE_NAME.split('_')[0].toLowerCase()
}

def defaultHealthCheckType(String t){
    if(t == 'live') { return 'httpGet' }
    if(t == 'readi'){ return 'httpGet' }
}

def buildUserName(){
    /* Get User info
       Requeire 'build user vars' plugin
       See https://plugins.jenkins.io/build-user-vars-plugin for more information
    */

    wrap([$class: 'BuildUser']) { 
        return BUILD_USER 
    }
}

def buildUserNameID(){
    wrap([$class: 'BuildUser']) { 
        return BUILD_USER_ID 
    }
}

def setGetCommitID(){
    Config.data.git_commit_id = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
}

def getMavenPackageInfo(String item){
    def pom = readMavenPom file: 'pom.xml'

    return pom.item
}

def getFileNameInfo(String f, String t='suffix'){
    return sh(script: "get-file-name $t $f", returnStdout: true).trim().toLowerCase()
}

def getGitRepoInfo(int id, String target='/'){
    def g    = new Git()
    def data = g.gitlabProjectInfo(
        Config.settings.git_hosts,
        Config.settings.git_credentials_api_token,
        Config.settings.git_timeout_seconds,
        id,
        target
    )
    
    return data
}

def getGitRepoAddress(int id){
    return data['http_url_to_repo'] = getGitRepo(id)
}

def k8sReplicas(){
    int repn = 2

    def dq = ['dev', 'uat', 'qa']
    if (ENVIRONMENT in dq) {
        repn =  1
    }

    return repn
}

def k8sDashboardURL(){
    def url = 'k8s.rulin.io'

    if(ENVIRONMENT != 'prd'){
        url = ENVIRONMENT.toLowerCase() + '-' + url
    }

    return url
}

def k8sNamespace(String n=companyName()){
    // company_backend_server
    // company_frontend_ui
    def ns = n + '-' + projectName()

    return ns
}