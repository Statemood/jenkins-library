/* Git.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.ci

def co(revision, repo, credentials){
    try {
        log.i 'Git clone ' + revision + ' from ' + repo

        git credentialsId: credentials,
            branch: revision,
            url: repo
    } 
    catch (e) {
        log.e 'Ops! Error occurred during git checkout'
        throw e
    }
}

def commitID(int len=41){
    return sh(script: 'git rev-parse HEAD', returnStdout: true).trim()[0..len]
}

def commitMessage(String cid){
    return sh(script: "git log --oneline --pretty='%H ## %s' | \
                       grep $cid                        | \
                       awk -F ' ## ' '{print \$2}'", 
              returnStdout: true).toString().trim()
}

def gitlabProjectInfo(String host, String token, Integer tos, Integer id, String target='/'){
    url  = host + '/api/v4/projects/' + id + target

    timeout(time: tos, unit: 'SECONDS') {
        withCredentials([string(credentialsId: token, variable: 'TOKEN')]) {
            private header = "'PRIVATE-TOKEN: $TOKEN'"
            private    cmd = "curl -s --header " + header + " " + url
            private    raw = sh(script:  cmd, returnStdout: true)
            private   data = readJSON returnPojo: true, text: raw

            return data
        }
    }
}