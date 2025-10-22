def call(Map cfg = [:]) {
  String label = cfg.label ?: 'pc'
  String path  = cfg.scriptPath ?: '/home/jenkins/top_ram.sh'

  pipeline {
    agent { label label }
    stages {
      stage('Show RAM Usage') {
        steps {
          sh "chmod +x '${path}' || true"
          sh "'${path}'"
        }
      }
    }
  }
}