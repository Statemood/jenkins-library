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
        log.err 'File ' + f + ' does not exist.'

    }
}

def isdir(String d) {
    def directory = new File(d)
    if (!directory.exists() || ! directory.isDirectory()) {
        log.err directory + ' does not exist or is not a directory.'
    }
}

def exist(String e) {
    if (!fileExists(e)) {
        log.err 'No such file or directory: ' + e
    }
}

def var(v) {
    if (!v) {
        log.err 'Require ' + v
    }
}

return this