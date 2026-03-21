pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'reservation-system'
        DOCKER_TAG = "v${BUILD_NUMBER}"
        NEXUS_URL = 'localhost:8081'
        SONAR_HOST = 'http://host.docker.internal:9000'
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${PATH}"
    }

    stages {