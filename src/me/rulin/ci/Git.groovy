/* Git.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def checkout(repo, revision) {
    check.var(repo)
    check.var(revision)

    try {
        log.i "Git checkout $revision ($repo)"
        
        checkout([$class: 'GitSCM',
                branches: [[name: revision]],
                userRemoteConfigs: [[url: repo]]])

        return
    } catch (e) {
        log.e "Ops! Error occurred during git checkout"
        throw e
    }
}

def commitID(){
    git_commit_id = sh(script: "git rev-parse HEAD", returnStdout: true).toString().trim()
    
    return git_commit_id
}

def commitIDShort(){
    git_commit_id_short = commitID()[0..GIT_COMMIT_ID_DISPLAY_LEN]

    return git_commit_id_short
}

def commitMessage(){
    git_c_id = commitID()
    git_commit_message = sh(script: "git log --oneline --pretty='%H ## %s' | \
                                     grep $git_c_id                        | \
                                     awk -F ' ## ' '{print \$2}'", returnStdout: true).toString().trim()
    
    return git_commit_message
}