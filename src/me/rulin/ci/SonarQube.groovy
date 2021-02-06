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
            private ssc_k = ' -Dsonar.projectKey='       + Config.data.base_name
            private ssc_n = ' -Dsonar.projectName='      + Config.data.base_name
            private ssc_v = ' -Dsonar.projectVersion='   + Config.data.git_revision
            private ssc_l = ' -Dsonar.language='         + Config.data.build_language
            private ssc_d = ' -Dsonar.projectBaseDir=.'
            private ssc_s = ' -Dsonar.sources=.'
            private ssc_b = ' -Dsonar.java.binaries=.'
                
            sonar_opts  = ssc_k + ssc_n + ssc_v + ssc_d + ssc_l + ssc_s + ssc_b
            sonar_opts += o
            sonar_exec  = 'sonar-scanner ' + sonar_opts  

            sh(sonar_exec)
        }
    }
    catch (e) {
        log.e 'Failed with SonarQube Scanner'
        throw e
    }
}

def qualityGateStatus(){
    try {
        timeout(time: 10, unit: 'MINUTES') { 
            def qg_stats = waitForQualityGate()

            if (qg_stats.status != 'SUCCESS') {
                log.err 'Pipeline aborted due to quality gate failure: ' + qg.stats
            }
        }
    }
    catch (e) {
        throw e
    }
}