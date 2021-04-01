package io.rulin.artifact 

def harborArtifactInfo(
            String tag, 
            String _id, 
            String registry, 
            String project, 
            String repo, 
            String args='?page=1&page_size=50', 
            Integer _timeout=10
    ){

    def private raw, res, auth, base, header
    header  = '"accept: application/json"'

    try {
        timeout(time: _timeout, unit: 'SECONDS'){
            withCredentials([string(credentialsId: _id, variable: 'HARBOR_BASIC_AUTH')]){
                auth = 'authorization: Basic ' + HARBOR_BASIC_AUTH
                base = 'https://' + registry + '/api/v2.0/projects/'
                url  =  base + project + '/repositories/' + repo + '/artifacts' + args
                cmd  =  'curl -s -H ' + header + ' -H "' + auth + '" ' + url
                raw  =  sh(script: cmd, returnStdout: true)
                res  =  readJSON text: raw

                return res
            }
        }
    }
    catch(e){
        log.e 'Error occurred during check tag exist'
        throw e
    }
}