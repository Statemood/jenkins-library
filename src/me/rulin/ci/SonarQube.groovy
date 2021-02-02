/* SonarQube.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

def scanner(String o="") {
    try {
        log.i "Preparing SonarQube Scanner"

        withCredentials([
            usernamePassword(
                credentialsId: 'Sonar-Account',
                passwordVariable: 'sonar_p',
                usernameVariable: 'sonar_u')])
        {
            private ssc_u = "-Dsonar.login="            + sonar_u
            private ssc_p = "-Dsonar.password="         + sonar_p
            private ssc_k = "-Dsonar.projectKey="       + Config.data.base_name
            private ssc_n = "-Dsonar.projectName="      + Config.data.base_name
            private ssc_v = "-Dsonar.projectVersion="   + Config.data.git_revision
            private ssc_d = "-Dsonar.projectBaseDir=."
            private ssc_l = "-Dsonar.language="         + Config.data.build_language
            private ssc_s = "-Dsonar.sources=."
            private ssc_b = "-Dsonar.java.binaries=."
        }
            
        sonar_opts  = ssc_u + ssc_p + ssc_k + ssc_n + ssc_v + ssc_d + ssc_l + ssc_s + ssc_b
        sonar_opts += o

        sh("sonar-scanner $sonar_opts")
    }
    catch (e) {
        log.e "Failed with Sonar Scanner"
        throw e
    }
}