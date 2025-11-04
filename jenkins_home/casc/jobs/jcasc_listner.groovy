pipelineJob('jcasc-listener') {
  description('Pipeline lắng nghe Generic Webhook (tạo bằng Job DSL)')

  definition {
    cps {
      script('''\
        pipeline {
          agent any

          triggers {
            genericTrigger(
              genericVariables: [
                [key: 'ref', value: '\\$.ref']
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
        ''')
      sandbox(true) // nếu bạn dùng Groovy sandbox
    }
  }

  // (không bắt buộc) Có thể gán thêm properties/labels… nếu muốn
}
