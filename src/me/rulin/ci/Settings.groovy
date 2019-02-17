/* Settings.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

class Settings {
    public static WORKSPACE     = JENKINS_HOME + "/workspace/" + JOB_NAME
    public static WS            = WORKSPACE
}