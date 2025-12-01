pipelineJob('job-A') {
    parameters {
        choiceParam('MESSAGE', ['Hello from Job A', 'Option 2', 'Option 3'], 'Select a message')
    }
    definition {
        cps {
            script('''
                @Library('my-shared-library') _
                
                pipeline {
                    agent any
                    stages {
                        stage('Print Message') {
                            steps {
                                printMessage(params.MESSAGE)
                            }
                        }
                    }
                }
            '''.stripIndent())
        }
    }
}

pipelineJob('job-B') {
    parameters {
        choiceParam('MESSAGE', ['Greetings from Job B', 'Choice B2', 'Choice B3'], 'Select a message')
    }
    definition {
        cps {
            script('''
                @Library('my-shared-library') _
                
                pipeline {
                    agent any
                    stages {
                        stage('Print Message') {
                            steps {
                                printMessage(params.MESSAGE)
                            }
                        }
                    }
                }
            '''.stripIndent())
        }
    }
}

pipelineJob('job-C') {
    parameters {
        choiceParam('MESSAGE', ['Welcome from Job C', 'Selection C2', 'Selection C3'], 'Select a message')
    }
    definition {
        cps {
            script('''
                @Library('my-shared-library') _
                
                pipeline {
                    agent any
                    stages {
                        stage('Print Message') {
                            steps {
                                printMessage(params.MESSAGE)
                            }
                        }
                    }
                }
            '''.stripIndent())
        }
    }
}
