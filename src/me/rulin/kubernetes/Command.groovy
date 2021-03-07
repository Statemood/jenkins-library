/* Command.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// Usage: 
//      k8s.command("create", "deployment")
//                             k8s.yaml

package me.rulin.kubernetes

def command(String cmd, String target=null){
    if (!cmd in Config.data.k8s_allowed_commands) {
        log.err 'Command not allowed: ' + cmd
    }

    if (target != null) {
        check.file(target)
    }

    log.i cmd.toUpperCase() + ' ' + target

    try {
        def private conf = Config.data.k8s_config_dir + Config.data.base_env.toLowerCase() + '.config'
        def private exec = 'kubectl --kubeconfig ' + conf + ' ' + cmd + ' -f ' + target

        check.file(conf)
        
        sh(script:  exec, label: 'Call kubectl for ' + cmd + ' ' + target)
    }
    catch (e) {
        log.e "Error occurred during exec 'kubectl' " + cmd + ' for target ' + target

        throw e
    }
}