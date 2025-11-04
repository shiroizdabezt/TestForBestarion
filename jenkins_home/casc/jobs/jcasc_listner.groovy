pipelineJob('casc-pipeline-5') {
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

  properties {
    disableConcurrentBuilds()
    pipelineTriggers([
    [$class: 'GenericTrigger',
      genericVariables: [
        [key: 'ADDED',    value: '$.commits[*].added[*]',    expressionType: 'JSONPath'],
        [key: 'MODIFIED', value: '$.commits[*].modified[*]', expressionType: 'JSONPath'],
        [key: 'REMOVED',  value: '$.commits[*].removed[*]',  expressionType: 'JSONPath'],
        [key: 'REF',      value: '$.ref',                     expressionType: 'JSONPath']
      ],
      token: 'abc',
      printContributedVariables: true,
      printPostContent: false,
      regexpFilterText: '$ADDED,$MODIFIED,$REMOVED|$REF',
      regexpFilterExpression: '(?s).*(?:^|,)(?:jenkins_home/casc/)[^,]+.*\\|refs/heads/main'
    ]
  ])
  }
}
