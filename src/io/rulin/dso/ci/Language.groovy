/* Language.groovy
   ##################################################
   # Created by Lin Ru at 2019.08.03 23:30          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.dso.ci
import  io.rulin.dso.ci.Build

def seletor(String lang, 
            String build_command,
            String build_options){
    private b = new Build()

    switch(lang.toLowerCase()){
        case 'java':
            b.build(build_options)
            return
        
        case ['golang', 'nodejs', 'php', 'python]']:
            b.build(build_options)
            return

        break
    }
}