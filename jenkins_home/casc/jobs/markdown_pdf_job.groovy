pipelineJob('markdown-to-pdf-conversion') {
    description('Job to convert Markdown files to PDF using Pandoc (Source from Git)')
    
    triggers {
        githubPush()
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/shiroizdabezt/TestForBestarion')
                        credentials('github-key')
                    }
                    branch('main')
                }
            }
            scriptPath('pdf_convert.jenkinsfile')
            lightweight(true)
        }
    }
}