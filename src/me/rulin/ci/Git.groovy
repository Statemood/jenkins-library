/* Git.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def commitID(int len=41){
    sh(script: "git rev-parse HEAD", returnStdout: true).trim()[0..len]
}

def commitMessage(){
    git_c_id = commitID()
    git_commit_message = sh(script: "git log --oneline --pretty='%H ## %s' | \
                                     grep $git_c_id                        | \
                                     awk -F ' ## ' '{print \$2}'", returnStdout: true).toString().trim()
    
    return git_commit_message
}