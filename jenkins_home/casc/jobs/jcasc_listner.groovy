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

  // Thêm job property "PipelineTriggersJobProperty" chứa GenericTrigger
  configure { node ->
    def props = node / 'properties'
    def triggersProp = props / 'org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty'
    def triggers = triggersProp / 'triggers'
    triggers / 'org.jenkinsci.plugins.gwt.GenericTrigger' {
      causeString('Triggered on $ref')
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
          key('ref')
          value('\\$.ref')      // quan trọng: escape $
          expressionType('JSONPath')
          defaultValue('')
          regexpFilter('')
        }
      }
    }
  }
}
