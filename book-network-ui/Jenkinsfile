pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = credentials('dockerhub-username') // DockerHub username stored in Jenkins credentials
        DOCKERHUB_TOKEN = credentials('dockerhub-token')       // DockerHub token stored in Jenkins credentials
        VPS_USERNAME = credentials('vps-username')            // VPS username stored in Jenkins credentials
        VPS_IP = credentials('vps-ip')                        // VPS IP address stored in Jenkins credentials
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Extract Project Version') {
            steps {
                dir('book-network-ui') {
                    script {
                        def packageJson = readJSON file: 'package.json'
                        env.VERSION = packageJson.version
                        echo "Extracted version: ${env.VERSION}"
                    }
                }
            }
        }

        stage('Login to DockerHub') {
            steps {
                script {
                    sh """
                        echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                    """
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                dir('book-network-ui') {
                    script {
                        sh """
                            docker build -t ${DOCKERHUB_USERNAME}/bsn-ui:latest -t ${DOCKERHUB_USERNAME}/bsn-ui:${env.VERSION} -f ../docker/frontend/Dockerfile .
                            docker push ${DOCKERHUB_USERNAME}/bsn-ui:latest
                            docker push ${DOCKERHUB_USERNAME}/bsn-ui:${env.VERSION}
                        """
                    }
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                script {
                    sh """
                        ssh ${VPS_USERNAME}@${VPS_IP} "mkdir -p ci-cd"
                        scp -oStrictHostKeyChecking=no docker-compose.yml ${VPS_USERNAME}@${VPS_IP}:ci-cd/docker-compose.yml
                        ssh ${VPS_USERNAME}@${VPS_IP} <<EOF
                        cd ci-cd
                        docker compose -f docker-compose.yml pull -q
                        docker compose -f docker-compose.yml up -d
                        EOF
                    """
                }
            }
        }

        // Uncomment the following stage if you want to include a health check
        /*
        stage('UI Health Check') {
            steps {
                script {
                    sh """
                        if ! docker ps -a | grep bsn-ui; then
                            echo "Container is not running"
                            exit 1
                        fi
                    """
                }
            }
        }
        */
    }
}
