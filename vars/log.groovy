/* log.groovy
    -----------------------------------------------------------
    Created by Lin Ru at 2018.10.01 22:00

    A Part of the Project JenkinsPipelineSharedLibrary
      https://github.com/Statemood/JenkinsPipelineSharedLibrary
    -----------------------------------------------------------
*/

def info(String msg) {
    echo "INFO: $msg"
}

def notice(String msg) {
    echo "NOTICE: $msg"
}

def warning(String msg) {
    echo "WARNING: $msg"
}

def error(String msg) {
    error msg
}

return this