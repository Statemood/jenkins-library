/* config.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

///////////////////////////////////////////////////////
// BASIC                                             //
///////////////////////////////////////////////////////
env.WORKSPACE       = JENKINS_HOME + "/workspace/" + JOB_NAME

// Get User info
wrap([$class: 'BuildUser']) { env.BUILD_USER    = BUILD_USER    }
wrap([$class: 'BuildUser']) { env.BUILD_USER_ID = BUILD_USER_ID }

WEB_ROOT            = "/data/web/"

// TRIGGERS
SKIP_BUILD          = false
SKIP_DOCKER         = false
SKIP_GIT            = false
SKIP_SONAR          = false
SKIP_K8S            = false     // kubernetes
SKIP_HPA            = false     // kubernetes HPA
SKIP_CRONJOB        = false     // kubernetes CronJob
SKIP_JOB            = false     // kubernetes Job

///////////////////////////////////////////////////////
// BUILD SETTINGS                                    //
///////////////////////////////////////////////////////
// Common
BUILD_DIR           = "."
BUILD_COMMAND       = ""
BUILD_LEGACY        = false

// Golang
env.GOROOT          = "/usr/lib/go"
env.GOPATH          =  WORKSPACE

// Maven
MAVEN_CMD           = "mvn"
MAVEN_OPTS          = "-U clean -Dmaven.test.skip=true package dependency:tree"

// NodeJS
NPM_REGISTRY        = "https://registry.npm.taobao.org/"
NPM_INSTALL         = "npm -i " + NPM_REGISTRY
NPM_I               =  NPM_INSTALL

// PHP (Composer)
PHP_COMPOSER_CMD    = "composer install"


// OSS
OSS_ENDPOINT        = ""
OSS_OPTIONS         = "-rf"

///////////////////////////////////////////////////////
// Docker                                            //
///////////////////////////////////////////////////////
DOCKER_REGISTRY                 = ""
DOCKER_REGISTRY_USER            = ""
// Saved in the Jenkins credentials with name 'Docker-Registry'
DOCKER_REGISTRY_PASSWORD        = credentials('Docker-Registry')
DOCKER_IMAGE_NAME               = ""
DOCKER_IMAGE_TAG                = ""
DOCKER_IMAGE_BP_TIMEOUT         = 300
DOCKER_IMAGE_BUILD_OPTIONS      = ""

DOCKERIGNORE_FILE               = ".dockerignore"

// Dockerfiles dir
DOCKERFILES                     = "Dockerfiles"

DOCKERFILE_TEMPLATE_LIST        = [DOCKERFILE_CUSTOMIZE, DOCKERFILE_LINK, DOCKERFILE_LANGUAGE, DOCKERFILE_DEFAULT]

///////////////////////////////////////////////////////
// kubernetes                                        //
///////////////////////////////////////////////////////
K8S_RUN_USER_ID                 = 
K8S_NAMESPACE                   = 
K8S_REPLICAS                    = 

K8S_AFFINITY_PROVIDER           = 
K8S_AFFINITY_ROLE               =
K8S_AFFINITY_REGION             = 

K8S_IMAGE_PULL_POLICY           = "IfNotPresent"
K8S_REVISION_HISTORY_LIMIT      = 5
K8S_STRATEGY_MAX_SURGE          = "10%"
K8S_STRATEGY_MAX_UNAVAILABLE    = "10%"
K8S_GRACE_PERIOD_SECONDS        = 60  // Sec
K8S_MIN_READY_SECONDS           = 5   // Sec

// ######################################################
// # Define your own Settings in SETTINGS to REPLACE ME #
// ######################################################
// Load SETTINGS if defined
if (SETTINGS) {
    if (fileExists(SETTINGS)) {
        log.notice "Loading $SETTINGS"
        load(SETTINGS)
    }
}