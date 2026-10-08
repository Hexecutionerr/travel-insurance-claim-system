pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'staging', 'prod'], description: 'Target Deployment Environment')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                bat 'call mvnw.cmd clean compile'
            }
        }
        stage('Test') {
            steps {
                bat 'call mvnw.cmd test'
            }
        }
        stage('Package') {
            steps {
                bat 'call mvnw.cmd package -DskipTests'
            }
        }
        stage('Deploy') {
            steps {
                echo "Deployment preparation completed for environment: ${params.DEPLOY_ENV}"
            }
        }
    }
}
