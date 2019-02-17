#! groovy

import me.rulin.ci.Docker


def call(args) {
    docker_name = args
    if (args == null || (args instanceof String && args.trim().isEmpty())) {
        docker_name = '123' //Docker.DOCKER_NAME 
    }

    echo "The name is: " + docker_name

    //Docker.Version()

    Docker dockeri = Docker.images()
    println dockeri
}