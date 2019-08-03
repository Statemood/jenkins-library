/* Language.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.03 23:30          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci
import  me.rulin.ci.Language

def build(String b_file=null){
    log.i "Preparing to build " + Config.data['app.lang'] + " project"

    private  bc = Config.data['app.build.command']
    private  bo = Config.data['app.build.options']
    private cmd = bc + " " + bo

    try {
        if (b_file){
            check.file(b_file)

            log.i "Build with command: " + bc + ", options: " + bo
            sh(cmd)
        }
    }
    catch (e) {
        throw e
    }
}

def seletor(String tl){
    // tl, target language
    private compile = new Language()

    switch(tl){
        case "php":
            compile.build('composer.json')
        
        case "java":
            switch(Config.data['app.build.command']) {
                case "ant":
                    compile.build()

                case "gradle":
                    compile.build('build.gradle')

                case "mvn":
                    compile.build('pom.xml')
                
                break
            }
        
        case "python":
            compile.build()

        case "golang":
            compile.build()

        case "nodejs":
            compile.build('package.json')

        break
    }
}