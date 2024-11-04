/* OSS.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.dso.utils

def sign(String bucket, 
         String config, 
         String target, 
         String endpiont='oss-cn-shanghai.aliyuncs.com', 
         String opts='--disable-encode-slash'){
    try {
        def private cmd = 'ossutil64'
        def private ocs = cmd + ' sign oss://' + bucket + target + ' -c ' + config + ' -e ' + endpiont + ' ' + opts

        sh(script: ocs, returnStdout: true)
    }
    catch (e) {
        private emsg = 'Oops! Error occurred during sign to OSS file.'
        metis.reason(emsg)
        log.e emsg

        throw e
    }
}

def upload(String bucket, 
           String config, 
           String src,
           String dest,
           String method='sync',
           String opts='-f'){
    try {
        def private cmd = 'ossutil64 ' + method + ' ' + opts +  ' ' + src + ' oss://' + bucket + dest + ' -c ' + config

        log.a 'Upload ' + src + ' to ' + bucket + ':' + dest + ', args: ' + opts

        sh(script: cmd, returnStdout: true)
    }
    catch (e) {
        log.e 'Oops! Error occurred during upload to OSS.'

        throw e
    }
}

def setmeta(String bucket, 
            String config, 
            String remote, 
            String policy){
    def private cmd = 'ossutil64 -c ' + config + ' set-meta oss://' + bucket + remote + ' ' + policy + ' -rf'

    try {
        sh(script: cmd, returnStdout: true)
    }
    catch (e) {
        log.e 'Oops! Error occurred during set meta.'

        throw e
    }
}