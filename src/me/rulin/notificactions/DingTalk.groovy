package me.rulin.notifications

def notice(String t, String u){
    timeout(time: Config.data.notice_timeout, unit: 'SECONDS') {
        withCredentials([
            string(
                credentialsId: 'DINGTALK_ACCESS_TOKEN', variable: 'DINGTALK_ACCESS_TOKEN'
            )
        ]){
            dingtalk accessToken: DINGTALK_ACCESS_TOKEN,
                title: t,
                messageUrl: u,
                picUrl: Config.data.notification_icon
        }
    }
}

def approve(){
    timeout(time: Config.data.notice_timeout, unit: 'SECONDS') {
        withCredentials([
            string(
                credentialsId: 'DINGTALK_ACCESS_TOKEN', 
                variable: 'DINGTALK_ACCESS_TOKEN'
            )
        ]){
            dingtalk 
                accessToken: DINGTALK_ACCESS_TOKEN,
                title: t,
                messageUrl: u,
                picUrl: Config.data.notification_icon
        }
    }
}