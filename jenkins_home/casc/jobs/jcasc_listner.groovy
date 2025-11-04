pipelineJob('casc-pipeline-4') {
  description('JCasC managed pipeline for testin')

  definition {
    cpsScm {
      scm {
        git {
          remote {
            url("https://github.com/shiroizdabezt/TestForBestarion.git")
            credentials('github-key')
          }
          branch('*/**')
        }
      }
      scriptPath('jenkins_home/casc/Jenkinsfile')
      lightweight(true)
    }
  }

properties([
    pipelineTriggers([
      [$class: 'GenericTrigger',
        genericVariables: [
          [key: 'ref',    value: '\$.ref',     expressionType: 'JSONPath', regexpFilter: '', defaultValue: ''],
          [key: 'before', value: '\$.before',  expressionType: 'JSONPath', regexpFilter: '', defaultValue: '']
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
        regexpFilterExpression: "refs/heads/${env.BRANCH_NAME}"
      ]
    ])
  ])
}
