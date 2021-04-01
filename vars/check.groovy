/* check.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import io.rulin.ci.Git

def file(String f) {
    if (!fileExists(f)) {
        log.err 'File ' + f + ' does not exist.'

    }
}

def isdir(String d) {
    def directory = new File(d)
    if (!directory.exists() || ! directory.isDirectory()) {
        log.err directory + ' does not exist or is not a directory.'
    }
}

def exist(String e) {
    if (!fileExists(e)) {
        log.err 'No such file or directory: ' + e
    }
}

def var(v) {
    if (!v) {
        log.err 'Require ' + v
    }
}

def permission(String u) {
    log.a 'Initiating permission check for build.'

    def private deny = true
    def private user = u.split('@')[0]
    def private data = metis.getGitRepoInfo(Config.data.git_repo_id, '/users')

    try{
        if(user in ['rulin', 'admin']){
            log.i 'System Administrators is always allowed.'
            return
        }

        if(user in data['username']){
            log.i 'Permission OK, start pipeline. '
            return
        }
        else {
            def errs = ', you do not have permission to build this job. ' 
            log.err 'Dear ' + Config.data.build_user + errs + 'A notification has been sent to Admins.'
        }
    }
    catch(e) {
        log.e 'Error occurred during permission check.'
        throw e
    }
}

return this