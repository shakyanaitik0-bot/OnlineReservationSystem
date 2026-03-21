pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'reservation-system'
        DOCKER_TAG = "v${BUILD_NUMBER}"
        NEXUS_URL = 'localhost:8081'
        SONAR_HOST = 'http://host.docker.internal:9000'
    }

    stages {

        stage('Clone') {
            steps {
                echo '--- Cloning repository ---'
                git branch: 'main',
                    url: 'https://github.com/shakyanaitik0-bot/OnlineReservationSystem.git'
            }
        }

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
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '--- Running SonarQube Scan ---'
                sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=online-reservation-system \
                    -Dsonar.host.url=${SONAR_HOST} \
                    -Dsonar.login=admin \
                    -Dsonar.password=admin
                """
            }
        }

        stage('OWASP Security Scan') {
            steps {
                echo '--- Running OWASP Security Scan ---'
                sh 'mvn org.owasp:dependency-check-maven:check'
            }
        }

        stage('Docker Build') {
            steps {
                echo '--- Building Docker Image ---'
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }

        stage('Push to Nexus') {
            steps {
                echo '--- Pushing to Nexus ---'
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${NEXUS_URL}/${DOCKER_IMAGE}:${DOCKER_TAG}"
                sh "docker push ${NEXUS_URL}/${DOCKER_IMAGE}:${DOCKER_TAG}"
            }
        }

        stage('Deploy') {
            steps {
                echo '--- Deploying Container ---'
                sh "docker stop reservation-app || true"
                sh "docker rm reservation-app || true"
                sh "docker run -d --name reservation-app -p 8090:8090 ${DOCKER_IMAGE}:${DOCKER_TAG}"
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