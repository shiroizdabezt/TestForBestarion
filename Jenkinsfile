pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
            }
        }
        stage('Test') {
            steps {
                pwd
            }
        }
        stage('Deploy') {
            steps {
                whoami
            }
        }
    }  
}