/* Build.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.jenkins

def build() {
    dir(BUILD_DIR) {
        try {
            switch (PROJECT_LANG) {
                case "PHP":
                    if (fileExists('composer.lock') || fileExists('composer.json')) {
                        log.info "Build PHP project with '$PHP_COMPOSER_CMD'"

                        sh(PHP_COMPOSER_CMD)
                    } else {
                        log.notice "Skip build for the general PHP project"
                    }
                
                case "NodeJS":
                    if (fileExists('package.json')) {
                        log.info "Build NodeJS project with '$NPM_I'"

                        sh(NPM_I)
                    }
            }

            if (BUILD_COMMAND) {
                log.info "Start $BUILD_COMMAND"

                sh(BUILD_COMMAND)
                
                return
            }
        } catch (e) {
            error e
        }
    }
}