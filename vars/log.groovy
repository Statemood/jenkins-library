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

def output(String level, String alias, String extra=null) {
    def private        t = 'me/rulin/locale/en_US/LC_MESSAGES.json'
    def String file_text = libraryResource(t)
    def private msg_text = readJSON text: file_text

    println msg_text

    println msg_text[alias]

    println level + ': ' + msg_text[alias][message] + extra
}


return this