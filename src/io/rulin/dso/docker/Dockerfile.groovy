/* Dockerfile.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package io.fosun.it.docker

def generate(Map d={:},
             Boolean skip_docker_copy=false,
             Boolean skip_docker_dfi_generate=false){

    def private ig_text     = libraryResource(d.templates_resources + 'dockerfile/.dockerignore')
    def private  dfc        = []
    def private dest        = d.artifact_dest
    def private base        = d.base_company + '/' + d.base_project + '/' + d.base_name
    def private temp        = d.templates_local + base + '/Dockerfiles'
    def private docker_dir  = d.docker_files_dir
    def private docker_file = docker_dir + 'Dockerfile.jenkins'

    log.i 'Using default ' + ignore

    writeFile file: ignore, text: ig_text
    
    metis.remove([docker_dir])

    sh('mkdir -p ' + docker_dir)

    if(d.artifact_src == '.' || d.hosted_by == 'nginx'){
        dest = d.base_root
        if(d.artifact_dest != ''){
            dest = d.artifact_dest
        }
    }

    if(fileExists(temp)){
        log.i 'Use exist project templates from ' + temp
        sh("cp -rf $temp/* $docker_dir")
    }

    if(!fileExists(docker_file)){
        def private bl = d.build_language 

        if(d.build_language_version != 0){
             bl = d.build_language + '/' + d.build_language_version
             log.a 'Dockerfile version: ' + bl
        }

        if(d.hosted_by == 'nginx') {
             bl = 'static'
        }
        
        def private dtf = templates_resources + 'dockerfile/language/' + bl + '/Dockerfile'
        def private txt = libraryResource(dtf)

        log.i 'Using default Dockerfile for ' + build_language
        log.i 'Copied ' + docker_file

        writeFile file: docker_file, text: txt
    }

    log.i 'Set dest to ' + dest

    // Check dockerfile is exist
    check.file(docker_file)

    // Set labels 
    def private labels  = 'LABEL made.by=Jenkins job.name=' + JOB_NAME + ' build.user.id=' + build_userid
                labels += ' git.revision=' + git_revision + ' git.commit.id=' + git_commit_id

    // Add commands to LIST dfc
    dfc.add(labels)
    if(!skip_docker_dfi_generate){
        dfc.add("RUN mkdir -p " + base_root)
        dfc.add("WORKDIR      " + base_root)

        if(!skip_docker_copy){
            dfc.add("COPY         " + artifact_src + " " + dest)
        }

        if(dockerfile_ext_opts){
            dfc.add(dockerfile_ext_opts)
        }

        dfc.add("USER " + run_user)

        // Add an empty line.
        sh("echo >> $docker_file")
    }

    // Generate Dockerfile
    for(ic in dfc){
        sh("echo $ic >> $docker_file")
    }

    //sh("echo RUN rm -rf $docker_dir")

    log.i 'Created ' + docker_file
}