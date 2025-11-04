pipelineJob('generic-test') {
  description('Job test Generic Webhook Trigger - bật sẵn trigger khi seed.')
  quietPeriod(0)

  definition {
    cps {
      script('''\
pipeline {
  agent any
  stages {
    stage('Echo') {
      steps {
        sh 'echo "Webhook triggered!"'
        sh 'echo "User: $user_name"'
        sh 'echo "Ref: $ref"'
        sh 'echo "Commit: $commit"'
      }
    }
  }
}
''')
      sandbox(true)
    }
  }

  // ⚙️ Phần này là chìa khóa: chèn cấu hình trigger vào XML để nó "bật sẵn"
  configure { node ->
    def props = node / 'properties'
    def triggersProp = props / 'org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty'
    def triggers = triggersProp / 'triggers'
    triggers / 'org.jenkinsci.plugins.gwt.GenericTrigger' {
      causeString('Triggered by $user_name on $ref ($commit)')
      token('abc123')
      tokenCredentialId('')
      printContributedVariables(true)
      printPostContent(true)
      silentResponse(false)
      shouldNotFlatten(false)
      regexpFilterText('$ref')
      regexpFilterExpression('.*')
      genericVariables {
        'org.jenkinsci.plugins.gwt.GenericVariable' {
          key('user_name')
          value('$.user_name')
          expressionType('JSONPath')
          defaultValue('')
          regexpFilter('')
        }
        'org.jenkinsci.plugins.gwt.GenericVariable' {
          key('ref')
          value('$.ref')
          expressionType('JSONPath')
          defaultValue('')
          regexpFilter('')
        }
        'org.jenkinsci.plugins.gwt.GenericVariable' {
          key('commit')
          value('$.after')
          expressionType('JSONPath')
          defaultValue('')
          regexpFilter('')
        }
      }
    }
  }
}
