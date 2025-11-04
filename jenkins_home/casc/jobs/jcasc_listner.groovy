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

triggers {
  genericTrigger {
    genericVariables {
      genericVariable {
        key("ADDED")
        value("\$.commits[*].added[*]")
        expressionType("JSONPath")
        regexpFilter("")
        defaultValue("")
      }
      genericVariable {
        key("MODIFIED")
        value("\$.commits[*].modified[*]")
        expressionType("JSONPath")
        regexpFilter("")
        defaultValue("")
      }
      genericVariable {
        key("REMOVED")
        value("\$.commits[*].removed[*]")
        expressionType("JSONPath")
        regexpFilter("")
        defaultValue("")
      }
      genericVariable {
        key("REF")
        value("\$.ref")
        expressionType("JSONPath")
        regexpFilter("")
        defaultValue("")
      }
    }
    genericRequestVariables { }
    genericHeaderVariables { }

    token('abc')
    tokenCredentialId('')
    printContributedVariables(true)
    printPostContent(false)
    silentResponse(false)
    shouldNotFlatten(false)

    // Lọc chỉ khi có thay đổi trong jenkins_home/casc/ và nhánh main
    regexpFilterText("\$ADDED,\$MODIFIED,\$REMOVED|\$REF")
    regexpFilterExpression("(?s).*(?:^|,)(?:jenkins_home/casc/)[^,]+.*\\|refs/heads/main")
  }
}

  properties {
    disableConcurrentBuilds()
  }
}
