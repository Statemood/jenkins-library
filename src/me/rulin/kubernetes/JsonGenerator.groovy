/* Yaml.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

package me.rulin.kubernetes

import  me.rulin.kubernetes.Location

def deployment(String f){
    try {
        def private location = new Location()
        def private        j = readJSON file: f
        def private        d = j[location.kind("Deployment", "json", f)]

        // Container + loop, support more containers
        def private deploy = [
            "apiVersion": "apps/v1",
            "kind": "Deployment",
            "metadata": [
                "labels": [
                    "app": APP_NAME,
                    "build-by": JOB_NAME,
                    "version": GIT_REVISION + "-" + Config.data['commit.id']
                ],
                "name": APP_NAME,
                "namespace": K8S_NAMESPACE
            ],
            "spec": [
                "progressDeadlineSeconds": K8S_PROGRESS_DEADLINE_SECONDS.toInteger(),
                "replicas": K8S_REPLICAS.toInteger(),
                "revisionHistoryLimit": K8S_REVISION_HISTORY_LIMIT.toInteger(),
                "selector": [
                    "matchLabels": [
                        "app": APP_NAME
                    ]
                ],
                "strategy": [
                    "rollingUpdate": [
                        "maxSurge": K8S_STRATEGY_MAX_SURGE,
                        "maxUnavailable": K8S_STRATEGY_MAX_UNAVAILABLE
                    ],
                    "type": "RollingUpdate"
                ],
                "template": [
                    "metadata": [
                        "labels": [ "app": APP_NAME ]
                    ],
                    "spec": [
                        "containers": [[
                            "name": APP_NAME,
                            "image": DOCKER_IMAGE,
                            "imagePullPolicy": K8S_IMAGE_PULL_POLICY,
                            "livenessProbe": [
                                "httpGet": [
                                    "path": "/",
                                    "port": APP_PORT.toInteger()
                                ],
                                "failureThreshold": 3,
                                "initialDelaySeconds": 30,
                                "periodSeconds": 10,
                                "successThreshold": 1,
                                "timeoutSeconds": 30
                            ],
                            "readinessProbe": [
                                "httpGet": [
                                    "path": "/",
                                    "port": APP_PORT.toInteger()
                                ],
                                "failureThreshold": 3,
                                "initialDelaySeconds": 30,
                                "periodSeconds": 10,
                                "successThreshold": 1,
                                "timeoutSeconds": 30
                            ],
                            "ports": [
                                [
                                    "name": "http",
                                    "protocol": "TCP",
                                    "containerPort": APP_PORT.toInteger()
                                ]
                            ],
                            "resources": [
                                "limits": [
                                    "cpu": K8S_LIMITS_CPU,
                                    "memory": K8S_LIMITS_MEMORY
                                ],
                                "requests": [
                                    "cpu": K8S_REQUESTS_CPU,
                                    "memory": K8S_REQUESTS_MEMORY
                                ]
                            ]
                        ]],
                        "restartPolicy": "Always",
                        "terminationGracePeriodSeconds": K8S_GRACE_PERIOD_SECONDS.toInteger()
                    ]
                ]
            ]
        ]

        deploy["spec.template.spec.containers"] = [
            "env": [
                "name": "ENVIRONMENT"
                "value": ENVIRONMENT
            ]
        ]

        def private dj = deploy

        writeJSON file: f, json: dj, pretty: 4
    }
    catch (e) {
        throw e
    }
}

def service(String f){
    try {
        def private      yml = readYaml file: f
        def private location = locationKind("Service", f)
        def private      svc = yml[location]
        def private       md = svc.metadata
        def private        s = svc.spec

        md.name              = APP_NAME
        md.namespace         = K8S_NAMESPACE
        s.selector.app       = APP_NAME
        s.ports[0].name      = "http"
        s.ports[0].port      = APP_PORT.toInteger()

        writeYaml file: f, data: yml, overwrite: true
    }
    catch (e) {
        log.e "Oops! An error occurred during update serivce, file: " + f
        throw e
    }
}