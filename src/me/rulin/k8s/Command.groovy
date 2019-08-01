/* kubernetes.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// Usage: 
//      k8s.command("create", "deployment")

package me.rulin.k8s

def command(String cmd, String target=null){
    if (!cmd in K8S_ALLOWED_COMMANDS) {
        log.err "Command not allowed: " + cmd
    }

    if (target != null) {
        check.file(target + ".yaml")
    }

    log.info cmd.toUpperCase() + target

    try {
        sh(script: "kubectl $cmd -f ${target}.yaml", label: "Call kubectl for $cmd $target")
    }
}