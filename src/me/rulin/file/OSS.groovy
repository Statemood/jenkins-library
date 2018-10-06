/* OFS.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// For more information about the command 'ossutil', 
//     see https://help.aliyun.com/document_detail/50452.html

package me.rulin.file

def put(String local_file, String oss_path) {
    check.file(local_file)

    try {
        log.info "Put $local_file to $oss_path with option '$OSS_OPTIONS'"

        sh("ossutil64 cp $local_file $oss_path $OSS_OPTIONS")
    } catch (e) {
        log.error e
    }
}