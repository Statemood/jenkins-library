package me.rulin.ci

interface Command {
    
    def sh(def args)

    void command(String name, Closure block)
}