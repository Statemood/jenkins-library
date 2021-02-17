/* Docker.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.docker

def String cmd(String c){
    try {
        sh("sudo docker $c")
    }
    catch (e) {
        throw e
    }
}

def private genDockerfile(String f='Dockerfile', String t='.', String d=Config.data.base_web_root, String c=null){
    if (fileExists(Config.data.docker_ignore_file)) {
        log.i 'Copy dockerignore file'

        sh('cp -rf' + Config.data.docker_ignore_file + ' .')
    }
    else {
        log.w 'File not found: ' + Config.data.docker_ignore_file
    }
    
    // Test Dockerfile exist
    check.file(f)
    def private dfc  = []
    def private cid  = Config.data.git_commit_id
    def private user = Config.data.build_userid

    dfc.add("LABEL made.by=Jenkins job.name=$JOB_NAME build.user.id=$user commit.id=$cid")
    dfc.add("RUN mkdir -p $d")
    dfc.add("COPY $t $d")

    sh("echo >> $f")

    for(s in dfc) {
        sh("echo $s >> $f")
    }
}

def private build(String image_name) {
    check.file('Dockerfile')
    try {
        log.info 'Build image: ' + image_name

        def private dibo = Config.data.docker_img_build_options

        timeout(time: Config.data.docker_img_build_timeout, unit: 'SECONDS') {
            cmd("build $dibo -t $image_name .")
        }
    }
    catch (e) {
        log.output("e",error_docker_build_image)
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
        println 'Error occurred during push image'
        throw e
    }
}

def login(String reg=DOCKER_REGISTRY, String opt=null){
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
        println 'Error occurred during login to registry'
        throw e
    }
}

def logout(){
    cmd('logout')
}