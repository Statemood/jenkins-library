/* Language.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.03 23:30          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci
import  me.rulin.ci.Build

def seletor(String tl){
    // tl, target language
    private b = new Build()

    switch(tl.toLowerCase()){
        case "php":
            b.build('composer.json')
            return
            
        case "java":
            switch(Config.data.build_command) {
                case "ant":
                    b.build()
                    return

                case "gradle":
                    b.build('build.gradle')
                    return

                case "mvn":
                    b.build('pom.xml')
                    return

                break
            }
        
        case "python":
            b.build()
            return

        case "golang":
            b.build()
            return

        case "nodejs":
            b.build('package.json')
            return
        break
    }
}