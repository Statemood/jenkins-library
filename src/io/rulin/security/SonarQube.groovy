/* SonarQube.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.security

def scanner(
    String name,
    String version,
    String lang,
    String cmd,
    String o='') {
    try {
        log.i 'Preparing SonarQube Scanner'
        withSonarQubeEnv(credentialsId: 'Sonar-Jenkins-Token'){
            def private opts 

            opts  = ' -Dsonar.projectKey='       + name
            opts += ' -Dsonar.projectName='      + name
            opts += ' -Dsonar.projectVersion='   + version
            opts += ' -Dsonar.language='         + lang
            opts += ' -Dsonar.projectBaseDir=.'
            opts += ' -Dsonar.sources=.'
            opts += ' -Dsonar.java.binaries=.'
            //opts += ' -Dsonar.java.coveragePlugin=jacoco'
            //opts += ' -Dsonar.jacoco.reportPath=jacoco.exec'
            //opts += ' -Dsonar.jacoco.itReportPath=jacoco.exec'

            sonar_exec  = cmd + opts + ' ' + o

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