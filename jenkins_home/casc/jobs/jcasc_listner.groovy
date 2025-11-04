pipeline {
  agent any

  triggers {
    genericTrigger(
      genericVariables: [
        [key: 'ref',    value: '$.ref',     expressionType: 'JSONPath', regexpFilter: '', defaultValue: ''],
        [key: 'before', value: '$.before',  expressionType: 'JSONPath', regexpFilter: '', defaultValue: '']
      ],
      genericRequestVariables: [
        [key: 'requestWithNumber', regexpFilter: '[^0-9]'],
        [key: 'requestWithString', regexpFilter: '']
      ],
      genericHeaderVariables: [
        [key: 'headerWithNumber', regexpFilter: '[^0-9]'],
        [key: 'headerWithString', regexpFilter: '']
      ],

      causeString: 'Triggered on $ref',

      token: 'abc123',
      tokenCredentialId: '',

      printContributedVariables: true,
      printPostContent: true,
      silentResponse: false,
      shouldNotFlatten: false,

      // chỉ nhận webhook khi ref khớp nhánh hiện tại
      regexpFilterText: '$ref',
      regexpFilterExpression: '.*'  // cho phép tất cả các nhánh
    )
  }

  stages {
    stage('Webhook received') {
      steps {
        echo "✅ Webhook triggered successfully on ref: ${ref}"
      }
    }
  }
}
