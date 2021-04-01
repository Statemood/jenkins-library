def markdown(String _id, Integer _timeout=30){
    timeout(time: _timeout, unit: 'SECONDS') {
        withCredentials(
            [string(credentialsId: _id, variable: 'DINGTALK_ACCESS_TOKEN')]
        ){
            def status  = currentBuild.currentResult
            def  time_t = currentBuild.durationString
            def       e = ENVIRONMENT.toUpperCase()
            def version = Config.data.docker_img_tag
            def    user = Config.data.build_user 
            def    name = Config.data.base_name
            def      ns = Config.data.base_project
            def  k8s_db = Config.data.k8s_dashboard_url
            def     now = sh(script: "date +'%F %T'", returnStdout: true).trim()
            def     url = 'https://oapi.dingtalk.com/robot/send?access_token=' + DINGTALK_ACCESS_TOKEN
            def  header = 'Content-Type: application/json'
            def     cmd = 'curl -s -H "' + header + '" ' + url + ' -d ' + "'{\"msgtype\": \"markdown\",\"markdown\": {\"title\": \"Jenkins ${status} build ${JOB_NAME} by ${user}\",\"text\": \"### *Jenkins* [${name}](${JOB_URL}) 由 ${user} 发布至${e}环境 \n>##### 名称：[${name}](${JOB_URL}) \n>##### 编号：[#${BUILD_NUMBER}](${BUILD_URL}) \n>##### 环境：[$ENVIRONMENT](https://${k8s_db}/#/deployment/${ns}/${name}?namespace=${ns})\n>##### 日志：[Console Output](${BUILD_URL}/console) \n>##### 版本：${version} \n>##### 状态：${status}\n>##### 耗时：${time_t}\n\n\n###### *[您可以点此查看服务运行状态](https://${k8s_db}/#/deployment/${ns}/${name}?namespace=${ns})* \n ###### ${now} [Jenkins](https://jenkins.fosun.com/)\n\"}}'"

            sh(script: cmd, returnStdout: true)
        }
    }
}

return this