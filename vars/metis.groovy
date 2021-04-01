import io.fosun.ci.Git

def getApplicationName(){
    return JOB_BASE_NAME.split('_')[1].toLowerCase()
}

def getDefaultPort(){
    return 8080
}

def getDefaultHealthCheckType(String t){
    if(t == 'live') { return 'httpGet' }
    if(t == 'readi'){ return 'httpGet' }
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
    Config.data.git_commit_id = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
}

def getReplicasNumber(){
    Integer repn = 2

    def dq = ['dev', 'uat', 'qa']
    if (ENVIRONMENT in dq) {
        repn =  1
    }

    return repn
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

def getKubernetesResources(String target, String res){
    // Default set for all envs
    def private req_c, req_m, lmt_c, lmt_m

    req_c = '0.2'
    req_m = '256Mi'
    lmt_c = '0.3'
    lmt_m = '512Mi'
            
    // For Product
    if(ENVIRONMENT == 'prd'){
        if(Config.data.build_language == 'java'){
            req_c = '1'
            req_m = '4Gi'
            lmt_c = req_c
            lmt_m = req_m
        }
        else {
            req_c = '0.3'
            req_m = '512Mi'
            lmt_c = req_c
            lmt_m = req_m
        }
    }
    else {
        // Java 
        if(Config.data.build_language == 'java'){
            req_c = '0.3'
            req_m = '512Mi'
            lmt_c = '0.5'
            lmt_m = '2Gi'
        }
    }

    switch(target.toLowerCase()) {
        case 'limits':
            if(res == 'cpu'   ){ return lmt_c }
            if(res == 'memory'){ return lmt_m }

        case 'requests':
            if(res == 'cpu'   ){ return req_c }
            if(res == 'memory'){ return req_m }

        break
    }
}

def k8sDashboardURL(){
    def url = 'k8s.rulin.io'

    if(ENVIRONMENT != 'prd'){
        url = ENVIRONMENT.toLowerCase() + '-' + url
    }

    return url
}