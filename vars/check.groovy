/* check.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def file(String f) {
    if (!fileExists(f)) {
        log.err "$f does not exist"

    }
}

def dir(String d) {
    def d = new File(d)
    if (!d.exists() || ! d.isDirectory()) {
        log.err "$d does not exist or is not a directory"
    }
}

def exist(String e) {
    if (!fileExists(e)) {
        log.err "$e: No such file or directory" 
    }
}

def var(v) {
    if (!v) {
        log.err "Require " + v
    }
}

return this