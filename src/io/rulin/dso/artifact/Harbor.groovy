/* Harbor.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.dso.artifact

// set artifactInfo / 
def artifactInfo(
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
            withCredentials([string(credentialsId: _id, variable: 'harbor_basic_auth')]){
                auth = 'authorization: Basic ' + harbor_basic_auth
                base = 'https://' + registry + '/api/v2.0/projects/'
                url  =  base + project + '/repositories/' + repo + '/artifacts' + args
                cmd  =  'curl -s -H ' + header + ' -H "' + auth + '" ' + url
                data =  sh(script: cmd, returnStdout: true)
                res  =  readJSON text: data

                return res
            }
        }
    }
    catch(e){
        log.e 'Error occurred during check tag exist'
        throw e
    }
}