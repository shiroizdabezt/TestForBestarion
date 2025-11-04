pipeline {
  agent any

  triggers {
    genericTrigger(
      genericVariables: [
        [key: 'ref', value: '$.ref']
      ],

      causeString: 'Triggered on $ref',

      token: 'abc123',
      tokenCredentialId: '',

      printContributedVariables: true,
      printPostContent: true,

      silentResponse: false,
      shouldNotFlatten: false,

      regexpFilterText: '$ref',
      regexpFilterExpression: ".*"
    )
  }

  stages {
    stage('Some step') {
      steps {
        sh 'echo "$ref"'
      }
    }
  }
}
