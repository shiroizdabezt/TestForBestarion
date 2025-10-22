def call(Map cfg = [:]) {
  def nodeLabel  = (cfg.label ?: 'pc')
  def scriptPath = (cfg.scriptPath ?: '/home/jenkins/top_ram.sh')

  pipeline {
    agent { label "${nodeLabel}" }
    stages {
      stage('Show RAM Usage') {
        steps {
          sh "chmod +x '${scriptPath}' || true"
          sh "'${scriptPath}'"
        }
      }
    }
  }
}