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
                                git url: 'https://github.com/shiroizdabezt/TestForBestarion', branch: 'main', credentialsId: 'github-key'
                            }
                        }
                        
                        stage('Check Commit Message') {
                            steps {
                                script {
                                    def msg = sh(returnStdout: true, script: 'git log -1 --pretty=%s').trim()
                                    echo "Commit message: ${msg}"
                                    
                                    if (!(msg ==~ /^doc(\\(.*\\))?: .*/)) {
                                        currentBuild.result = 'ABORTED'
                                        error("Skipping build: Commit message does not match pattern.")
                                    }
                                }
                            }
                        }
                        
                        stage('Convert to PDF') {
                            agent {
                                docker { 
                                    image 'pandoc/latex:latest' 
                                    args '-u root:root --entrypoint='
                                    reuseNode true
                                }
                            }
                            steps {
                                script {
                                    sh """
                                        apk add --no-cache ttf-dejavu || true
                                        
                                        find . -name "*.md" | while read file; do 
                                            echo "Converting \\${file}..."
                                            
                                            pandoc "\\${file}" \
                                            -o "\\${file%.md}.pdf" \
                                            --pdf-engine=xelatex \
                                            -V mainfont="DejaVu Sans"
                                        done
                                    """
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