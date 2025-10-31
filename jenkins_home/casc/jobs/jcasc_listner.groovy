pipelineJob('casc-pipeline-4') {
  description('JCasC managed pipeline for testing')

  definition {
    cpsScm {
      scm {
        git {
          remote {
            url("https://github.com/minhhieu16/bestarion-jenkins.git")
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
