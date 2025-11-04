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

  // ✅ đây là chỗ làm cho Generic Webhook Trigger được tick trong UI
  properties {
    pipelineTriggers([
      [$class: 'GenericTrigger',
        genericVariables: [[key: 'ref', value: '\\$.ref']],
        causeString: 'Triggered on $ref',
        token: 'abc123',
        tokenCredentialId: '',
        printContributedVariables: true,
        printPostContent: true,
        silentResponse: false,
        shouldNotFlatten: false,
        regexpFilterText: '$ref',
        regexpFilterExpression: '.*'
      ]
    ])
  }
}
