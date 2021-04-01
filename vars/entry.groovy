def call(Map config=[:]){
    node {
        settings.config(config)
    }

    pipeline {
        agent any

        options {
            //timestamps()
            skipDefaultCheckout()
            parallelsAlwaysFailFast()
            timeout(time: 60, unit: 'MINUTES')
            buildDiscarder(logRotator(numToKeepStr: '30'))
        }

        parameters {
            gitParameter (
                branch: '', 
                branchFilter: 'origin/(.*)', 
                defaultValue: 'master', 
                listSize: '10', 
                name: 'GIT_REVISION', 
                quickFilterEnabled: true, 
                selectedValue: 'NONE', 
                sortMode: 'DESCENDING_SMART', 
                tagFilter: '*', 
                type: 'PT_BRANCH_TAG', 
                description: 'Please select a branch or tag to build',
                useRepository: Config.data.git_repo)

            choice(
                name: 'ENVIRONMENT',
                description: 'Please select Environment',
                choices: 'dev\nqa\nuat\npre\nprd')

            choice(
                name: 'ACTION',
                description: 'Please select action',
                choices: 'deploy\nrollback')

            choice(
                name: 'DEPLOYMENT_MODE',
                description: 'Please select action',
                choices: 'Container\nLegacy\nMixed')
        }

        stages {
            stage ('Initial Pipeline') {
                steps {
                    script {
                        log.i 'Initiating Pipeline'
                        controller.entry()
                    }
                }
            }
        }

        post {
            aborted {
                script {
                    dingTalk.markdown('DingTalk-AccessToken-' + Config.data.base_project)
                }
            }
            always {
                script {
                    if(ENVIRONMENT == 'prd' || Config.data.notice_always){
                        dingTalk.markdown('DingTalk-AccessToken-' + Config.data.base_project)
                    }
                }
            }
            failure {
                script {
                    dingTalk.markdown('DingTalk-AccessToken-' + Config.data.base_project)
                }
            }
        }
    }
}