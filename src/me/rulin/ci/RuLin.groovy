package me.rulin.ci

import  me.rulin.ci.Command

class RuLin {
    private Command command

    RuLin(Command command) {
       this.command = command
    }

    static String execCommand(String c) {
        command.sh(
            script: "$c"
        )
    }
}