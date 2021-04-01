package io.rulin.cd

def upload(
    String pp, 
    String pkp, 
    String appid, 
    String uv, 
    Boolean es,
    String desc,
    int r=1){

    def private  ci = 'miniprogram-ci'
    def private cmd = ci + ' upload ' + '--pp ' + pp + ' --pkp ' + pkp + ' --appid ' + appid + 
                           ' --uv ' + uv + ' -r ' + r + ' --enable-es6 ' + es + ' --ud ' + "'$desc'"

    log.i 'Upload ' + pp
    
    try{
        sh(cmd)
    }
    catch (e){
        log.e 'Error occurred during upload to miniprogram.'
        throw e
    }
}