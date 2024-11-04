/* Gitlab.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.dso.ci

def projectInfo(String  host, 
                String  token, 
                Integer timeoutsec, 
                Integer id,
                String  target='/'){
    def url  = host + '/api/v4/projects/' + id + target

    timeout(time: timeoutsec, unit: 'SECONDS'){
        withCredentials([string(credentialsId: token, variable: 'gitlab_token')]){
            def private header = "'PRIVATE-TOKEN: $gitlab_token'"
            def private    cmd = "curl -s --header " + header + " " + url
            def private    raw = sh(script:  cmd, returnStdout: true)
            def private   data = readJSON returnPojo: true, text: raw

            return data
        }
    }
}