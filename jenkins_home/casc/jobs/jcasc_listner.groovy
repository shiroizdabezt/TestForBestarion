pipelineJob('casc-pipeline-4') {
  description('JCasC managed pipeline for testin')

  definition {
    cps {
      script('''
        node {
          properties([
            pipelineTriggers([
              [
                \$class: 'GenericTrigger',
                genericVariables: [
                  [key: 'ref',    value: '\\$.ref',     expressionType: 'JSONPath', regexpFilter: '', defaultValue: ''],
                  [key: 'before', value: '\\$.before',  expressionType: 'JSONPath', regexpFilter: '', defaultValue: '']
                ],
                genericRequestVariables: [
                  [key: 'requestWithNumber', regexpFilter: '[^0-9]'],
                  [key: 'requestWithString', regexpFilter: '']
                ],
                genericHeaderVariables: [
                  [key: 'headerWithNumber', regexpFilter: '[^0-9]'],
                  [key: 'headerWithString', regexpFilter: '']
                ],
                causeString: 'Triggered on \$ref',
                token: 'abc123',
                tokenCredentialId: '',
                printContributedVariables: true,
                printPostContent: true,
                silentResponse: false,
                shouldNotFlatten: false,
                regexpFilterText: '\$ref',
                regexpFilterExpression: '.*'
              ]
            ])
          ])

          echo "✅ Webhook triggered for ref: \$ref"
        }
      '''.stripIndent())
      sandbox(true)
    }
  }
}
