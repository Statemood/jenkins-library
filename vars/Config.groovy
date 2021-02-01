/* Config.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// CI Config
class Config implements Serializable {
    static Map data     = [:]

    static Map settings = [
        'base'                      : [
            'dir'                   : first_dir,
            'name'                  : app_name,
            'port'                  : 8080,
            'workspace'             : JENKINS_HOME + "/workspace/" + JOB_NAME
        ],
        'build'                     : [
            'action'                : ACTION,
            'command'               : "mvn",
            'dir'                   : ".",
            'env'                   : ENVIRONMENT,
            'legacy'                : false,
            'options'               : "-ff -ntp -U clean -Dmaven.test.skip=true package dependency:tree",
            'skip'                  : false,
            'stage'                 : "master",
            'user'                  : BUILD_USER,
            'userid'                : BUILD_USER_ID
        ],
        'docker'                    : [
            'account'               : "Docker-Registry",
            'image'                 : [
                'name'              : "",
                'tag'               : "",
                'build_timeout'     : 1800,
                'build_options'     : "",
                'push_timeout'      : 600
            ],
            'ignore_file'           : ".dockerignore"
        ],
        'git'                       : [
            'commit_length'         : 8,
            'credentials'           : "DefaultGitSCMCredentialsID",
            'repo'                  : null,
            'revision'              : GIT_REVISION,
            'skip'                  : false,
            'stage'                 : "master"
        ],
        'k8s'                       : [
            'namespace'             : project_name,
            'resources'             : [
                'limits_cpu'        : "300m",
                'limits_memory'     : "512Mi",
                'requests_cpu'      : "300m",
                'requests_memory'   : "512Mi"
            ],
            'run'                   : [
                'args'              : null,
                'command'           : null,
                'user'              : null
            ]
        ]
    ]
}