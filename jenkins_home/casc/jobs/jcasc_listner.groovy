pipelineJob('casc-pipeline') {
  description('JCasC managed pipeline for testing')

  definition {
    cpsScm {
      scm {
        git {
          remote {
            url("${GITHUB_BRANCH}")
            credentials('github-key')
          }
          branch('*/**')
        }
      }
      scriptPath('jenkins_home/casc/Jenkinsfile')
    }
  }

  triggers {
    githubPush()
  }

  properties {
    disableConcurrentBuilds()
  }
}
