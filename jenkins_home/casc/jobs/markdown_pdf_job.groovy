pipelineJob('markdown-to-pdf-conversion') {
    description('Job to convert Markdown files to PDF using Pandoc, triggered by specific commit messages.')
    
    triggers {
        githubPush()
    }

    definition {
        cps {
            script('''
                pipeline {
                    agent any
                    
                    stages {
                        stage('Checkout') {
                            steps {
                                git url: 'https://github.com/shiroizdabezt/TestForBestarion', branch: 'main'
                            }
                        }
                        
                        stage('Check Commit Message') {
                            steps {
                                script {
                                    def msg = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
                                    echo "Commit message: ${msg}"
                                    
                                    if (!(msg ==~ /^doc(\\(.*\\))?: .*/)) {
                                        currentBuild.result = 'ABORTED'
                                        error("Skipping build: Commit message '${msg}' does not match pattern 'doc: ...' or 'doc(...): ...'")
                                    }
                                }
                            }
                        }
                        
                        stage('Convert to PDF') {
                            agent {
                                docker { 
                                    image 'pandoc/core' 
                                    args '--entrypoint='
                                }
                            }
                            steps {
                                script {
                                    // Find all markdown files and convert them
                                    sh '''
                                        find . -name "*.md" | while read file; do
                                            echo "Converting ${file}..."
                                            pandoc "${file}" -o "${file%.md}.pdf"
                                        done
                                    '''
                                }
                            }
                        }
                        
                        stage('Archive Artifacts') {
                            steps {
                                archiveArtifacts artifacts: '**/*.pdf', allowEmptyArchive: true
                            }
                        }
                    }
                }
            '''.stripIndent())
            sandbox(true)
        }
    }
}
