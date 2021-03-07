package me.rulin.cd

def upload(String source_file = '.', 
           String dest_path = Config.data.base_web_root, 
           String dest_name = Config.data.base_name,
           String host = null, 
           String port = Config.data.ssh_port, 
           String user = Config.data.ssh_user){
 
    def private pth = dest_path + '/' + dest_name
    def private ssc = 'rsync'
    def private arg = ssc + ' -au -l ' + Config.data.ssh_speed_limit + ' --port ' + port
    def private cmd = arg + source_file + ' ' + user + '@' + host + ':' + pth

    log.i 'Start upload ' + source_file ' to ' + host + ':' + port + ':' + pth

    sh(cmd)

    log.i 'Upload completed successfully.'
}

def offline(){
    log.i 'Offline'
}

def online(){
    log.i 'Online'
}

def restart(){
    log.i 'restart'
}

def start(){
    log.i 'start'
}

def stop(){
    log.i 'stop'
}