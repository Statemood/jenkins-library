/* controller.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

import io.rulin.artifact.Harbor
import io.rulin.cd.Server
import io.rulin.cd.WeixinMiniProgram
import io.rulin.ci.Git
import io.rulin.ci.Language
import io.rulin.ci.SonarQube
import io.rulin.docker.Docker
import io.rulin.kubernetes.Command
import io.rulin.kubernetes.Yaml

def entry() {
    /*
    Order:
        1. Local Settings
        2. .jenkins.yaml
        3. Config
    */

    preProcess()
    codeClone()

    def private scan_after_build = ['java']
    def                        d = Config.data
    def private   none_container = false
    def private             lang = Config.data.build_language.toLowerCase()
    def private          version = Config.data.build_language_version
    def private        node_name = lang

    if(version != 0){ node_name = lang + '-' + version }

    d.build_node_name = node_name

    if(d.hosted_by.toLowerCase() != 'container' && DEPLOYMENT_MODE.toLowerCase() == 'legacy' ){
        none_container      = true 
    } 
    else {
        d.docker_img_tag    = d.git_revision    + '-' + d.git_commit_id[0..8]
        d.docker_img_name   = d.docker_registry + '/' + d.base_project + '/' + d.base_name
        d.docker_img        = d.docker_img_name + ':' + d.docker_img_tag
    }

    if(d.hosted_by.toLowerCase() == 'mmp'){
        none_container      = true
    }

    if(isImageExist()){
        log.a 'Artifact version ' + d.docker_img_tag + ' already exist, skip build.'
        d.skip_build        = true
        d.skip_docker       = true
    }

    if(none_container){
        d.skip_docker       = true 
        d.skip_k8s          = true
    }

    if(d.build_language in scan_after_build) {
        codeBuild()
    
        parallel (
            'Test'          : { codeTest()  },
            'Sonar Scan'    : { sonarScan() },
            'Docker'        : { doDocker()  }
        )
    }
    else {
        sonarScan()
        codeBuild()
        parallel (
            'Test'          : { codeTest()  },
            'Sonar Scan'    : { sonarScan() },
            'Docker'        : { doDocker()  }
        )
    }

    if(!d.skip_deploy){
        doKubernetes()
        deploy2MMP()
    }
}

// Set build info
def currentBuildInfo(){
    def private name = BUILD_NUMBER + '-' + Config.data.base_env.toUpperCase()
    def private desc = Config.data.build_user + ' ' + Config.data.base_action + ' ' + Config.data.git_revision

    currentBuild.displayName = name 
    currentBuild.description = desc 
}

def preProcess() {
    stage('Pre-Process') {
        node(Config.data.stage_pre_process) {
            dir(Config.data.base_dir) {
                // Set default info
                // Set build info
                // Check parameters
                currentBuildInfo()
                if(Config.data.check_permission){
                    check.permission(Config.data.build_userid)
                }
                log.i 'Stage Pre-Process OK'
            }
        }
    }
}

def codeClone() {
    stage('Git Clone') {
        def private d = Config.data
        def private g = new Git()
        if(!d.skip_git){
            node(d.stage_git) {
                dir(d.base_dir) {
                    g.co(d.git_revision, d.git_repo, d.git_credentials_clone)
                    metis.getGetCommitID()
                }
            }
        }
        else {
            log.a 'Skipped stage git clone.'
        }
    }
}

def sonarScan() {
    stage('SonarQube Scanner') {
        if(!Config.data.skip_sonar){
            node(Config.data.build_node_name) {
                dir(Config.data.base_dir) {
                    def sonar = new SonarQube()
                    sonar.scanner(Config.data.sonar_scanner)
                }
            }
        }
        else {
            log.a 'Sikpped stage SonarQube Scan'
        }
    }
}

def qualityGate(){
    stage("Quality Gate") {
        node(Config.data.build_node_name) {
            timeout(time: 30, unit: 'MINUTES') {
                dir(Config.data.base_dir) {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}

def isImageExist(){
    def private harbor = new Harbor()
    def private  found = false
    def private      d = Config.data
    def private    tag = d.git_revision + '-' + d.git_commit_id[0..8]
    def private    res = harbor.artifactInfo(
                            tag,
                            d.docker_registry_basic_auth,
                            d.docker_registry,
                            d.base_project,
                            d.base_name
                        )
                        
    res.each{
        t = it['tags']
        if(t != 'null'){
            t.each{lists->
                if(lists['name'] == tag){
                    found = true
                    return true 
                }
                return false
            }
        }
    }
    return found
}

def codeBuild() {
    if(!Config.data.skip_build){
        stage('Build Code') {
            node(Config.data.build_node_name) {
                dir(Config.data.base_dir) {
                    def language = new Language()
                    language.seletor(Config.data.build_language)
                }
            }
        }
    }
}

def codeTest() {
    def private utc = Config.data.test_command + ' ' + Config.data.test_options
    if (!Config.data.skip_test) {
        stage('Test') {
            node(Config.data.build_node_name = node_name) {
                dir(Config.data.base_dir) {
                    log.i 'Test by command: ' + utc

                    sh(utc)
                }
            }
        }
    }
}

def doDocker(){
    def private d = Config.data
    if(DEPLOYMENT_MODE == 'Legacy' || d.skip_docker) { return }
    stage('Docker') {
        node(d.stage_docker) {
            dir(d.base_dir) {
                def private docker = new Docker()
                parallel (
                    'Generate Dockerfile': { docker.genDockerfile() },
                    'Login to Registry'  : { docker.login() }
                )
                
                docker.build(d.docker_img)
                docker.push(d.docker_img)
            }
        }
    }
}

def doKubernetes(){
    if(DEPLOYMENT_MODE == 'Legacy' || Config.data.skip_k8s) { return }
    stage('Kubernetes') {
        node(Config.data.stage_k8s) {
            dir(Config.data.base_dir) {
                def cmd     = new Command()
                def gen     = new Yaml()
                def pth     = Config.data.k8s_yml_default_dir
                def deploy  = Config.data.k8s_yml_default_deploy
                def service = Config.data.k8s_yml_default_svc

                parallel (
                    'Generate Deployment'   : { gen.deployment() },
                    'Generate Service'      : { gen.service()    }
                )
                log.a 'Ready to deploy!'

                parallel (
                    'Deploy Deployment'     : { cmd.command('apply', pth + deploy)  },
                    'Deploy Service'        : { cmd.command('apply', pth + service) }
                )
            }
        }
    }
}

def doServer(List th=[]){
    if(DEPLOYMENT_MODE == 'Container') { return }

    def server = new Server()

    for(host in th){
        server.upload()
    }
}

def deploy2MMP(String dist='./dist/'){
    if(Config.data.hosted_by == 'mmp' && Config.data.build_language == 'nodejs'){
        stage('Deploy to MiniProgram'){
            node(Config.data.build_node_name){
                def private project_config = 'dist/project.config.json'

                check.file(project_config)

                def private  cfg = readJSON file: project_config
                def private  mmp = new WeixinMiniProgram()
                def private  cid = 'Private-Key-' + Config.data.base_name + '-' + Config.data.base_project + '-' + ENVIRONMENT
                def private desc = 'Upload by '   + Config.data.build_user + ', Jenkins ID #' + BUILD_NUMBER + 
                                    '. Version '  + Config.data.git_revision + ', commit id: ' + Config.data.git_commit_id + '.'

                withCredentials([file(credentialsId: cid, variable: 'MMP_PRIVATE_KEY')]){
                    check.file(MMP_PRIVATE_KEY)
                    mmp.upload(
                        dist,
                        MMP_PRIVATE_KEY,
                        cfg['appid'],
                        Config.data.git_revision + '.' + ENVIRONMENT.toLowerCase(),
                        cfg.setting['es6'],
                        desc
                    )
                }
            }
        }
    }
}

return this