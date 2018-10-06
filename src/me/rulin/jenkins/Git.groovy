/* git.groovy
    -----------------------------------------------------------
    Created by Lin Ru at 2018.10.01 22:00

    A Part of the Project JenkinsPipelineSharedLibrary
      https://github.com/Statemood/JenkinsPipelineSharedLibrary
    -----------------------------------------------------------
*/

package me.rulin.jenkins

def checkoutCode(String GIT_REPO, SCM_REVISION) {
    check.var(GIT_REPO)
    check.var(SCM_REVISION)

    try {
        checkout([$class: 'GitSCM',
                branches: [[name: SCM_REVISION]],
                userRemoteConfigs: [[url: GIT_REPO]]])

        GIT_COMMIT_ID   = sh(returnStdout: true, script: "git rev-parse HEAD").toString().trim()
        GIT_COMMIT_MSG  = sh(returnStdout: true, script: "git log --oneline --pretty='%H ## %s' | \
                                                          grep $GIT_COMMIT_ID                   | \
                                                          awk -F ' ## ' '{print \$2}'").toString().trim()
        log.info "Git: $GIT_REPO\nType: $SCM_TYPE\nCheckout: $GIT_CO_PATH\nRevision: $SCM_REVISION\nCommit ID: $GIT_COMMIT_ID"

        return
    } catch (e) {
        echo "Ops! Error occurred during git checkout"
        error e
    }
}