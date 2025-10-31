pipelineJob('casc-pipeline-3') {
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
      sandbox()
    }
  }

  triggers {
    githubPush()
  }

  properties {
    disableConcurrentBuilds()
  }
}
