pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'reservation-system'
        DOCKER_TAG = "v${BUILD_NUMBER}"
        SONAR_HOST = 'http://host.docker.internal:9000'
    }

    stages {

        stage('Clone') {
            steps {
                echo '--- Cloning repository ---'
                git branch: 'main', url: 'https://github.com/shakyanaitik0-bot/OnlineReservationSystem.git'
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

        stage('SonarQube Analysis') {
            steps {
                echo '--- Running SonarQube Scan ---'
                sh '/usr/share/maven/bin/mvn sonar:sonar -Dsonar.projectKey=online-reservation-system -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.login=admin -Dsonar.password=admin123'
            }
        }

        stage('OWASP Security Scan') {
            steps {
                echo '--- Running OWASP Security Scan ---'
                sh '/usr/share/maven/bin/mvn org.owasp:dependency-check-maven:check || true'
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
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Check logs above.'
        }
    }
}