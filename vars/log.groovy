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

return this