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
        sh 'echo "abcaawfawfawfafa"'
      }
    }
  }
}
''')
      sandbox(true)
    }
  }
    triggers {
    genericTrigger {
      genericVariables {
        genericVariable { key('ref');      value('$.ref') }
        genericVariable { key('added');    value('$.commits..added[*]') }
        genericVariable { key('modified'); value('$.commits..modified[*]') }
        genericVariable { key('removed');  value('$.commits..removed[*]') }
      }
      token("abc123")
      printContributedVariables(true)
      printPostContent(true)
      silentResponse(false)
      regexpFilterText('$ref $added $modified $removed')
      regexpFilterExpression('^refs/heads/test\\b.*jenkins_home/casc/jobs/')

      causeString('Triggered on $ref with changes under jenkins_home/casc/jobs/')
    }
  }
}
