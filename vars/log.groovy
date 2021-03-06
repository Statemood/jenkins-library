/* log.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

def       a(String msg) { echo "ATTENTION: $msg"   }

def       i(String msg) { info(msg)                }

def       n(String msg) { notice(msg)              } 

def       w(String msg) { warning(msg)             }

def       e(String msg) { echo "$msg"              }

def    info(String msg) { echo "INFO: $msg"        }

def  notice(String msg) { echo "NOTICE: $msg"      }

def warning(String msg) { echo "WARNING: $msg"     }

def     err(String msg) { error " $msg"            }

def output(String level, String alias, String extra='') {
    def private        t = 'me/rulin/locale/' + Config.data.locale + '/LC_MESSAGES.json'
    def String file_text = libraryResource(t)
    def private      msg = readJSON text: file_text

    println level + ': ' + msg[alias].message + extra
}

def messages(){
    Map dict = [
        init_load_default_settings      : "Default settings loaded.",
        init_load_settings_file         : "Load settings file."
    ]
}

return this