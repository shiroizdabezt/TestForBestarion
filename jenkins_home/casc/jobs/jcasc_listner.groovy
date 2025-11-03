pipelineJob('casc-pipeline-4') {
  description('JCasC managed pipeline for testing')

  definition {
    cpsScm {
      scm {
        git {
          remote {
            url("https://github.com/shiroizdabezt/TestForBestarion.git")
            credentials('github-key')
          }
          branch('*/**')
          extensions {
            pathRestriction {
              includedRegions('^jenkins_home/casc/.*')
            }
          }
        }
      }
      scriptPath('jenkins_home/casc/Jenkinsfile')
      lightweight(true)
    }
  }

  triggers {
    githubPush()
  }

  properties {
    disableConcurrentBuilds()
  }
}
