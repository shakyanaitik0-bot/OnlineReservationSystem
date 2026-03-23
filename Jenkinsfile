pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "reservation-app"
        DOCKER_TAG = "latest"
    }

    stages {

        stage('Clone') {
            steps {
                echo '--- Cloning repository ---'
                git 'https://github.com/shakyanaitik0-bot/OnlineReservationSystem.git'
            }
        }

        stage('Build') {
            steps {
                echo '--- Building with Maven ---'
                sh '/usr/share/maven/bin/mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo '--- Running Unit Tests ---'
                sh '/usr/share/maven/bin/mvn test'
            }
        }

        // ❌ SonarQube REMOVED COMPLETELY

        stage('Docker Build') {
            steps {
                echo '--- Building Docker Image ---'
                sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'
            }
        }

        stage('Deploy') {
            steps {
                echo '--- Deploying Container ---'
                sh 'docker stop reservation-app || true'
                sh 'docker rm reservation-app || true'
                sh 'docker run -d --name reservation-app -p 8090:8090 ${DOCKER_IMAGE}:${DOCKER_TAG}'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed! Check logs.'
        }
    }
}