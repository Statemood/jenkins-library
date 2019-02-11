/* kubernetes.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// Usage: 
//      kubernetes.command("create", "deployment")

def command(String cmd, String target=null){
    if (!cmd in K8S_ALLOWED_COMMANDS) {
        log.error "Command not allowed: " + cmd
    }

    if (target != null) {
        check.file(target + ".yaml")
    }

    log.info cmd.toUpperCase() + target

    try {
        sh("kubectl $cmd -f ${target}.yaml")
    }
}

return this