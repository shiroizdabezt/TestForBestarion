pipelineJob('jcasc-listener') {
  description('Pipeline lắng nghe Generic Webhook (tạo bằng Job DSL)')

  definition {
    cps {
      script('''\
pipeline {
  agent any
  stages {
    stage('Some step') {
      steps {
        sh 'echo "$ref"'
      }
    }
  }
}
''')
      sandbox(true)
    }
  }
}
