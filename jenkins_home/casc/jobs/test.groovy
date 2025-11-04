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
    triggers {
    genericTrigger {
      spec('')  // 👈 BẮT BUỘC có dòng này để trigger được bật trong UI

      genericVariables {
        genericVariable {
          key('user_name')
          value('$.user_name')
        }
        genericVariable {
          key('commit')
          value('$.after')
        }
        genericVariable {
          key('ref')
          value('$.ref')
        }
        genericVariable {
          key('object_kind')
          value('$.object_kind')
        }
      }
      token(repo.path)
      printContributedVariables(true)
      printPostContent(true)
      silentResponse(false)
      regexpFilterText('$object_kind $ref')
      regexpFilterExpression('^push refs/heads/' + repo.default_branch + '$')
      causeString('Triggered by $user_name who pushed $commit to $ref')
    }
  }
}
