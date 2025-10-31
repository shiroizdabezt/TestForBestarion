pipelineJob('hello-world') {
  description('A simple pipeline job created via Job DSL for testing')

  definition {
    cps {
      script("""
        pipeline {
          agent any
          stages {
            stage('Hello') {
              steps {
                echo "Hello from Job DSL!"
              }
            }
            stage('Date') {
              steps {
                sh 'date'
              }
            }
          }
        }
      """.stripIndent())
      sandbox(true)
    }
  }

  triggers {
    cron('H/30 * * * *') // chạy mỗi 30 phút
  }

  properties {
    disableConcurrentBuilds()
  }
}
