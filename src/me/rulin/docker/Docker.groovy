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

def private genDockerfile(String f=Config.data.docker_file, 
                          String s=Config.data.artifact_src,
                          String t=Config.data.artifact_dest,
                          String d=Config.data.base_web_root, 
                          String c=null){
    if (fileExists(Config.data.docker_ignore_file)) {
        log.i 'Copy dockerignore file'

        sh('cp -rf' + Config.data.docker_ignore_file + ' .')
    }
    else {
        log.w 'File not found: ' + Config.data.docker_ignore_file + '. Skipped.'
    }
    
    // Test Dockerfile exist
    sh("rm -fv $f")
    if(!fileExists(f)){
        log.i 'Using default Dockerfile for ' + Config.data.build_language
        def private        dtf = Config.data.base_templates_dir + '/dockerfile/language/' + 
                                 Config.data.build_language     + '/Dockerfile'
        def private String txt = libraryResource(dtf)

        log.i 'Copied ' + f

        writeFile file: f, text: txt
    }

    check.file(f)
    def private  dfc = []
    def private  cid = Config.data.git_commit_id
    def private buid = Config.data.build_userid
    def private user = Config.data.run_user
    def private  cmd = Config.data.run_command
    def private  rev = Config.data.git_revision
    def private dest = d + t

    if(Config.data.build_language == 'java'){
        dest = d + Config.data.base_name + '.' + metis.getFileNameInfo(s)
    }

    dfc.add("LABEL made.by=Jenkins job.name=$JOB_NAME build.user.id=$buid git.revision=$rev git.commit.id=$cid")
    dfc.add("RUN   mkdir -p $d")
    dfc.add("COPY    $s $dest")
    dfc.add("USER       $user")
    dfc.add("WORKDIR    $d")
    dfc.add("CMD        [$cmd]")

    // Keep this line.
    sh("echo >> $f")

    for(ic in dfc){
        sh("echo $ic >> $f")
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
        log.e 'Error occurred during login to registry'
        throw e
    }
}

def logout(){
    cmd('logout')
}