/* SonarQube.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def scanner(String sonar_opts=null) {
    try {
        log.i "Preparing SonarQube Scanner"

        if (sonar_opts == null) {
            withCredentials([usernamePassword(
                credentialsId: 'Sonar-Account', 
                passwordVariable: 'sonar_p', 
                usernameVariable: 'sonar_u')]) {
                    ssc_u = "-Dsonar.login="            + sonar_u
                    ssc_p = "-Dsonar.password="         + sonar_p
                    ssc_k = "-Dsonar.projectKey="       + APP_NAME
                    ssc_n = "-Dsonar.projectName="      + APP_NAME
                    ssc_v = "-Dsonar.projectVersion="   + Config.data['revision']
                    ssc_d = "-Dsonar.projectBaseDir=."
                    ssc_l = "-Dsonar.language="         + Config.data['lang']
                    ssc_s = "-Dsonar.sources=."
                    ssc_b = "-Dsonar.java.binaries=."
                }
            
            sonar_opts = ssc_u + ssc_p + ssc_k + ssc_n + ssc_v + ssc_d + ssc_l + ssc_s + ssc_b
            }
        sh("$SONAR_BIN $sonar_opts")
    }
    catch (e) {
        log.err "Failed with Sonar Scanner"
    }
}