/* Docker.groovy
   ##################################################
   # Created by Lin Ru at 2019.02.17 22:55          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.ci

private String cmd(String c){
    if(c) {
        try {
            sh("sudo docker $c")
        }
        catch (e) {
            throw e
        }
    }
    else {
        error "No docker cmd input"
    }
}

def images() {
    cmd("images")
}

def version(){
    cmd("version")
}

def build(build_args){
    sh("sudo docker build -t ")
}

def push(){
    cmd("push ")
}

def login(){
    cmd("login")
}

def logout(){
    cmd("logout")
}