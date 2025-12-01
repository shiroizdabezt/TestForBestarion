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
  }
}