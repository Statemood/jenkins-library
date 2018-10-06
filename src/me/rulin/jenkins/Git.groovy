/* git.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.jenkins

def checkoutCode(String GIT_REPO, SCM_REVISION) {
    check.var(GIT_REPO)
    check.var(SCM_REVISION)

    try {
        checkout([$class: 'GitSCM',
                branches: [[name: SCM_REVISION]],
                userRemoteConfigs: [[url: GIT_REPO]]])

        GIT_COMMIT_ID   = sh(script: "git rev-parse HEAD", returnStdout: true).toString().trim()
        GIT_COMMIT_MSG  = sh(script: "git log --oneline --pretty='%H ## %s' | \
                                      grep $GIT_COMMIT_ID                   | \
                                      awk -F ' ## ' '{print \$2}'", returnStdout: true).toString().trim()

        return
    } catch (e) {
        echo "Ops! Error occurred during git checkout"
        error e
    }
}