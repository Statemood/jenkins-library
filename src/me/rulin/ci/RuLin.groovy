package me.rulin.ci

class RuLin {
    private CMD cmd 

    RuLin(CMD cmd) {
       this.cmd = cmd
    }

    static String execCommand(String c) {
        cmd.sh(
            script: "$c"
        )
    }
}