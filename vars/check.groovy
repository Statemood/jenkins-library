/* check.groovy
    -----------------------------------------------------------
    Created by Lin Ru at 2018.10.01 22:00

    A Part of the Project JenkinsPipelineSharedLibrary
      https://github.com/Statemood/JenkinsPipelineSharedLibrary
    -----------------------------------------------------------
*/

def file(String f) {
    if (!fileExists(f)) {
        log.error "$f does not exist" 
    }
}

def dir(String d) {
    d = new File(d)
    if (!d.exists() || ! d.isDirectory()) {
        log.error "$d does not exist or is not a directory"
    }
}

def exist(String e) {
    if (!fileExists(e)) {
        log.error "$e: No such file or directory" 
    }
}

def var(v) {
    if (!v) {
        log.error "Require " + v
    }
}

return this