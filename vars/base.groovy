/* base.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def gitCheckout() {
    check.var(GIT_REPO)
    check.var(SCM_REVISION)

    try {
        checkout([$class: 'GitSCM',
                branches: [[name: SCM_REVISION]],
                userRemoteConfigs: [[url: GIT_REPO]]])

        return
    } catch (e) {
        log.error "Ops! Error occurred during git checkout"
        throw e
    }
}

def gitCommitID(){
    git_commit_id = sh(script: "git rev-parse HEAD", returnStdout: true).toString().trim()
    
    return git_commit_id
}

def gitCommitIDShort(){
    git_commit_id_short = git_commit_id[0..GIT_COMMIT_ID_DISPLAY_LEN]

    return git_commit_id_short
}

def gitCommitMessage(){
    git_commit_message = sh(script: "git log --oneline --pretty='%H ## %s' | \
                                     grep $GIT_COMMIT_ID                   | \
                                     awk -F ' ## ' '{print \$2}'", returnStdout: true).toString().trim()
    
    return git_commit_message
}

return this