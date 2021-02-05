/* SonarQube.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def scanner(String o='') {
    try {
        log.i 'Preparing SonarQube Scanner'
        withSonarQubeEnv(credentialsId: 'Sonar-Jenkins-Token'){
            /*
            withCredentials([
                usernamePassword(
                    credentialsId: 'Sonar-Account',
                    passwordVariable: 'sonar_p',
                    usernameVariable: 'sonar_u')])
            {
                */
                //private ssc_u = ' -Dsonar.login='            + sonar_u
                //private ssc_p = ' -Dsonar.password='         + sonar_p
                private ssc_k = ' -Dsonar.projectKey='       + Config.data.base_name
                private ssc_n = ' -Dsonar.projectName='      + Config.data.base_name
                private ssc_v = ' -Dsonar.projectVersion='   + Config.data.git_revision
                private ssc_d = ' -Dsonar.projectBaseDir=.'
                private ssc_l = ' -Dsonar.language='         + Config.data.build_language
                private ssc_s = ' -Dsonar.sources=.'
                private ssc_b = ' -Dsonar.java.binaries=.'
                
                sonar_opts  = ssc_k + ssc_n + ssc_v + ssc_d + ssc_l + ssc_s + ssc_b
                sonar_opts += o
                sonar_exec  = 'sonar-scanner ' + sonar_opts  

                sh(sonar_exec)

                log.i 'SonarQube done'
            //}
        }

        timeout(time: 1, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout
            def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv

            println 'gq = ' + gq
            if (qg.status != 'SUCCESS') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
        }
    }
    catch (e) {
        log.e 'Failed with Sonar Scanner'
        throw e
    }
}