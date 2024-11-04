/* Docker.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.rulin.dso.docker

def String cmd(String c){
    try {
        sh("sudo docker $c")
    }
    catch (e) {
        throw e
    }
}

def private build(String image_name) {
    check.file(Config.data.docker_file)
    try {
        log.info 'Build image: ' + image_name

        def private dibo = Config.data.docker_img_build_options
        def private   df = Config.data.docker_file

        timeout(time: Config.data.docker_img_build_timeout, unit: 'SECONDS') {
            cmd("build $dibo -t $image_name -f $df .")
        }
    }
    catch (e) {
        //log.output("e",error_docker_build_image)
        log.e 'Error occurred during build image'
        throw e
    }
}

def private push(String image_name){
    try {
        log.info 'Push image ' + image_name

        timeout(time: Config.data.docker_img_push_timeout, unit: 'SECONDS') {
            cmd("push $image_name")
        }
    }
    catch (e) {
        log.e 'Error occurred during push image'
        throw e
    }
}

def login(String reg=Config.data.docker_registry, String opt=null){
    try {
        log.i 'Login to Docker Registry ' + reg

        timeout(time: Config.data.docker_login_timeout, unit: 'SECONDS') {
            withCredentials([
                usernamePassword(
                    credentialsId: Config.data.docker_account,
                    passwordVariable: 'registry_password',
                    usernameVariable: 'registry_username'
                )
            ]){
                def login_args = 'login -u ' + registry_username + ' -p ' + registry_password + ' ' + reg
                cmd(login_args)
            }
        }
    }
    catch (e) {
        log.e 'Error occurred during login to registry'
        throw e
    }
}

def logout(){
    cmd('logout')
}