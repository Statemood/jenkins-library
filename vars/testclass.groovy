#! groovy

import me.rulin.ci.*


def call(args) {
    docker_name = args
    if (args == null || (args instanceof String && args.trim().isEmpty())) {
        docker_name = docker.Name.DOCKER_NAME 
    }

    echo "The name is: " + docker_name

    //Docker.Version()

    docker.Version.getVersion()
}