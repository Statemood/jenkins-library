/* FileOperation.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.dso.utils

def download(String url, String name, String credential){
    try {
        withCredentials([
            usernamePassword(
                credentialsId: credential,
                passwordVariable: 'password',
                usernameVariable: 'username'
        )]){
            log.i 'Download ' + name + ' from ' + url
            def private pkg  = 'curl -Ls --user "$username:$password" -w %{http_code} -o ' + name + ' ' + url
            def private stat = sh(script: pkg, returnStdout: true)
            if(stat == '404'){
                def emsg = 'File not found: ' + url
                
                metis.reason(emsg)
                log.err emsg
            }
            else{
                check.file(name)
                log.i 'File ' + name + ' downloaded.'
            }
        }
    }
    catch (e) {
        throw e
    }
}

def upload(String local_file,
           String remote_path,
           String remote_host, 
           String remote_port, 
           String remote_user,
           String speed_limit){
 
    try {
        def private arg = ' -au -l ' + speed_limit + ' --port ' + remote_port + ' '
        def private cmd = 'rsync' + arg + local_file + ' ' + remote_user + '@' + remote_host + ':' + remote_path

        log.i 'Start upload ' + local_file ' to ' + remote_host + ':' + remote_path

        sh(cmd)

        log.i 'Upload completed successfully.'
    }
    catch (e) {
        throw e
    }
}

