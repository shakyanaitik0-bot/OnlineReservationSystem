pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "reservation-app"
        DOCKER_TAG = "latest"
    }

    stages {

        stage('Build') {
            steps {
                echo '--- Building with Maven ---'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo '--- Running Unit Tests ---'
                sh 'mvn test'
            }
        }

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