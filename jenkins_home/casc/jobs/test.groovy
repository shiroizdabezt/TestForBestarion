pipelineJob('Max.Deploy.Performance.LinhLX') {
  description('Pipeline deploy performance services')

  definition {
    cps {
      script('''\
pipeline {
  agent {
    label 'jenkins-agent'
  }

  options {
    skipDefaultCheckout(true)
    buildDiscarder(logRotator(daysToKeepStr: '14', numToKeepStr: '50'))
  }

  environment {
    TARGET_ENV = 'perf'
    AWS_REGION = 'us-east-1'
    ECS_CLUSTER_NAME = "max-${TARGET_ENV}"
  }

  parameters {
    booleanParam(name: 'DEPLOY_BACKEND_API', defaultValue: false, description: 'Deploy service: backend-api')
    string(name: 'BACKEND_API_TASK_DEF_REVISION', defaultValue: '', description: 'Task Definition revision for backend-api (e.g., "123" or "family:123"). Leave empty to use latest.')
    
    booleanParam(name: 'DEPLOY_TRANSFER_SERVICE', defaultValue: false, description: 'Deploy service: transfer-service')
    string(name: 'TRANSFER_SERVICE_TASK_DEF_REVISION', defaultValue: '', description: 'Task Definition revision for transfer-service (e.g., "123" or "family:123"). Leave empty to use latest.')
    
    booleanParam(name: 'DEPLOY_WORKER_CONSUMER', defaultValue: false, description: 'Deploy service: worker-loaddata-consumer')
    string(name: 'WORKER_CONSUMER_TASK_DEF_REVISION', defaultValue: '', description: 'Task Definition revision for worker-loaddata-consumer (e.g., "123" or "family:123"). Leave empty to use latest.')
    
    booleanParam(name: 'DEPLOY_WORKER_MONITOR', defaultValue: false, description: 'Deploy service: worker-loaddata-monitor')
    string(name: 'WORKER_MONITOR_TASK_DEF_REVISION', defaultValue: '', description: 'Task Definition revision for worker-loaddata-monitor (e.g., "123" or "family:123"). Leave empty to use latest.')
  }

  stages {
    stage('Initialize & Validate') {
      steps {
        script {
          echo "[INFO] ----------------------------------------"
          echo "[INFO] INITIALIZING JOB FOR ENVIRONMENT: ${env.TARGET_ENV}"
          echo "[INFO] Target cluster: ${env.ECS_CLUSTER_NAME}"
          echo "[INFO] ----------------------------------------"

          def servicesToDeploy = []
          def serviceRevisionMap = [:]
          
          if (params.DEPLOY_BACKEND_API) {
            servicesToDeploy.add('backend-api')
            serviceRevisionMap['backend-api'] = params.BACKEND_API_TASK_DEF_REVISION ?: ''
          }
          if (params.DEPLOY_TRANSFER_SERVICE) {
            servicesToDeploy.add('transfer-service')
            serviceRevisionMap['transfer-service'] = params.TRANSFER_SERVICE_TASK_DEF_REVISION ?: ''
          }
          if (params.DEPLOY_WORKER_CONSUMER) {
            servicesToDeploy.add('worker-loaddata-consumer')
            serviceRevisionMap['worker-loaddata-consumer'] = params.WORKER_CONSUMER_TASK_DEF_REVISION ?: ''
          }
          if (params.DEPLOY_WORKER_MONITOR) {
            servicesToDeploy.add('worker-loaddata-monitor')
            serviceRevisionMap['worker-loaddata-monitor'] = params.WORKER_MONITOR_TASK_DEF_REVISION ?: ''
          }

          if (servicesToDeploy.isEmpty()) {
            error "[ERROR] No service selected for deployment! Please select at least 1 service."
          }

          echo "[INFO] Services to be deployed: ${servicesToDeploy}"
          
          env.SELECTED_SERVICES = servicesToDeploy.join(',')
          
          def revisionMapString = serviceRevisionMap.collect { k, v -> "${k}:${v}" }.join('|')
          env.SERVICE_REVISION_MAP = revisionMapString

          echo "[INFO] Checking cluster on AWS..."
          
          def clusterArn = sh(
            script: """
              aws ecs list-clusters --region ${env.AWS_REGION} \\
              --query "clusterArns[?contains(@, '${env.ECS_CLUSTER_NAME}')] | [0]" --output text
            """,
            returnStdout: true
          ).trim()

          if (clusterArn == "None" || !clusterArn) {
             error "[ERROR] Cluster '${env.ECS_CLUSTER_NAME}' does not exist in region ${env.AWS_REGION}. Please check TARGET_ENV variable."
          }
          
          env.ECS_CLUSTER_ARN = clusterArn
          echo "[INFO] Valid cluster ARN: ${env.ECS_CLUSTER_ARN}"
          
          def servicesList = servicesToDeploy.join(', ')
          def revisionInfo = serviceRevisionMap.collect { k, v -> 
            def rev = v ?: 'latest'
            "${k}: ${rev}"
          }.join(', ')
          
          def startMessage = """
*Deployment Started*
Environment: *${env.TARGET_ENV}*
Services: ${servicesList}
Task Definition Revisions: ${revisionInfo}
Build: ${env.BUILD_URL}
          """.stripIndent()
          
          slackNotify(startMessage)
        }
      }
    }

    stage('Parallel Deploy Services') {
      steps {
        script {
          def services = env.SELECTED_SERVICES.split(',')
          def parallelTasks = [:]
          
          def serviceRevisionMap = [:]
          if (env.SERVICE_REVISION_MAP) {
            env.SERVICE_REVISION_MAP.split('\\|').each { entry ->
              def parts = entry.split(':', 2)
              if (parts.length == 2) {
                serviceRevisionMap[parts[0]] = parts[1]
              }
            }
          }

          services.each { serviceName ->
            def currentService = serviceName 
            def taskDefRevision = serviceRevisionMap[currentService] ?: ''
            
            parallelTasks["Deploy ${currentService}"] = {
              stage("Process: ${currentService}") {
                script {
                  deployServiceLogic(currentService, env.ECS_CLUSTER_ARN, env.AWS_REGION, env.TARGET_ENV, taskDefRevision)
                }
              }
            }
          }

          parallel parallelTasks
        }
      }
    }
  }
}

def deployServiceLogic(serviceName, clusterArn, region, envName, taskDefRevision = '') {
  def pfx = "[${serviceName}]"

  echo "${pfx} START PROCESSING..."
  
  try {
    def serviceArn = sh(
      script: """
        set +x
        aws ecs list-services --cluster ${clusterArn} --region ${region} \\
        --query "serviceArns[?contains(@, '${serviceName}')] | [0]" --output text
      """,
      returnStdout: true
    ).trim()

    if (serviceArn == "None" || !serviceArn) {
      echo "${pfx} [ERROR] Service '${serviceName}' not found in cluster."
      currentBuild.result = 'UNSTABLE'
      return
    }
    
    echo "${pfx} [INFO] Service ARN: ${serviceArn.split('/').last()}"

    def ssmPath = "/max/${envName}/${serviceName}/image_name"
    
    echo "${pfx} [INFO] Auto SSM Path: ${ssmPath}"
    
    def newImage = ""
    try {
      newImage = sh(
        script: """
          set +x
          aws ssm get-parameter --name "${ssmPath}" --region ${region} \\
          --query 'Parameter.Value' --output text
        """,
        returnStdout: true
      ).trim()
    } catch (Exception e) {
      echo "${pfx} [ERROR] SSM Parameter not found!"
      echo "${pfx} [DEBUG] Please check path: ${ssmPath}"
      throw e
    }

    if (!newImage) error("Image value from SSM is empty")
    
    echo "${pfx} [INFO] Image found: ${newImage}"

    def taskDefArn = ''
    
    if (taskDefRevision && taskDefRevision.trim()) {
      def revision = taskDefRevision.trim()
      
      if (revision.contains(':')) {
        taskDefArn = revision
        echo "${pfx} [INFO] Using specified Task Definition: ${taskDefArn}"
      } else {
        def currentTaskDefArn = sh(
          script: """
            set +x
            aws ecs describe-services --cluster ${clusterArn} --services ${serviceArn} --region ${region} \\
            --query 'services[0].taskDefinition' --output text
          """,
          returnStdout: true
        ).trim()
        
        if (currentTaskDefArn == "None" || !currentTaskDefArn) {
          echo "${pfx} [ERROR] Failed to get current Task Definition from service to extract family name."
          currentBuild.result = 'UNSTABLE'
          return
        }
        
        def familyName = currentTaskDefArn.split('/')[1].split(':')[0]
        taskDefArn = "${familyName}:${revision}"
        echo "${pfx} [INFO] Using specified revision ${revision} with family ${familyName}"
        echo "${pfx} [INFO] Task Definition: ${taskDefArn}"
      }
    } else {
      def currentTaskDefArn = sh(
        script: """
          set +x
          aws ecs describe-services --cluster ${clusterArn} --services ${serviceArn} --region ${region} \\
          --query 'services[0].taskDefinition' --output text
        """,
        returnStdout: true
      ).trim()

      if (currentTaskDefArn == "None" || !currentTaskDefArn) {
        echo "${pfx} [ERROR] Failed to get current Task Definition from service to extract family name."
        currentBuild.result = 'UNSTABLE'
        return
      }

      def familyName = currentTaskDefArn.split('/')[1].split(':')[0]
      taskDefArn = familyName
      echo "${pfx} [INFO] Using latest Task Definition of family: ${familyName}"
    }

    def taskDefJson = sh(
      script: """
        set +x
        aws ecs describe-task-definition --task-definition ${taskDefArn} --region ${region} --include TAGS
      """,
      returnStdout: true
    ).trim()

    def workspaceDir = env.WORKSPACE ?: sh(script: 'pwd', returnStdout: true).trim()
    def outputDir = "${workspaceDir}/task-definitions/${envName}"

    sh(script: "mkdir -p '${outputDir}'")
    
    def tempJsonFile = "${outputDir}/${serviceName}_temp.json"
    def updatedJsonFile = "${outputDir}/${serviceName}.json"
    writeFile file: tempJsonFile, text: taskDefJson
    
    def pythonScript = """
import json
import sys
import os

temp_file = '${tempJsonFile}'
output_file = '${updatedJsonFile}'
new_image = '${newImage}'

try:
    with open(temp_file, 'r') as f:
        data = json.load(f)
    
    task_def = data.get('taskDefinition', data)
    
    if 'containerDefinitions' not in task_def or len(task_def['containerDefinitions']) == 0:
        print("ERROR: No containerDefinitions found!", file=sys.stderr)
        sys.exit(1)
    
    old_image = task_def['containerDefinitions'][0].get('image', '')
    task_def['containerDefinitions'][0]['image'] = new_image
    print(f"Updated image: {old_image} -> {new_image}")
    
    tags = data.get('tags', [])
    if not tags and 'tags' in task_def:
        tags = task_def.get('tags', [])
    
    allowed_keys = {
        "family",
        "taskRoleArn",
        "executionRoleArn",
        "networkMode",
        "containerDefinitions",
        "volumes",
        "placementConstraints",
        "requiresCompatibilities",
        "cpu",
        "memory",
        "tags",
        "pidMode",
        "ipcMode",
        "proxyConfiguration",
        "inferenceAccelerators",
        "ephemeralStorage",
        "runtimePlatform",
        "enableFaultInjection"
    }
    register_payload = {k: v for k, v in task_def.items() if k in allowed_keys}
    
    if tags:
        register_payload['tags'] = tags
        print(f"Found {len(tags)} tag(s) to preserve: {tags}")
    else:
        print("No tags found in task definition response")
    
    with open(output_file, 'w') as f:
        json.dump(register_payload, f, indent=2)
    
    print(f"Task definition saved to: {output_file}")
    
except Exception as e:
    print(f"ERROR: {str(e)}", file=sys.stderr)
    sys.exit(1)
"""
    
    def pythonFile = "${outputDir}/${serviceName}_update.py"
    writeFile file: pythonFile, text: pythonScript
    
    sh(script: "python3 '${pythonFile}'")
    
    sh(script: "rm -f '${tempJsonFile}' '${pythonFile}'", returnStatus: true)

    echo "${pfx} [INFO] Task Definition has been updated and saved at: ${outputDir}/${serviceName}.json"

    def newTaskDefArn = sh(
      script: """
        set +x
        aws ecs register-task-definition --cli-input-json file://${updatedJsonFile} \\
        --query 'taskDefinition.taskDefinitionArn' --output text
      """,
      returnStdout: true
    ).trim()

    if (!newTaskDefArn || newTaskDefArn == "None") {
      error("${pfx} [ERROR] Failed to get ARN of new Task Definition after registration.")
    }

    echo "${pfx} [INFO] New Task Definition registered: ${newTaskDefArn}"

    sh(
      script: """
        set +x
        aws ecs update-service --cluster ${clusterArn} --service ${serviceArn} \\
        --task-definition ${newTaskDefArn} --force-new-deployment
      """
    )
    echo "${pfx} [INFO] Service has been updated successfully with new task definition."
    
    echo "${pfx} [SUCCESS] Deploy Service Completed."
    
    def revisionDisplay = taskDefRevision ?: 'latest'
    def successMessage = """
*Deployment Success*
Environment: *${envName}*
Service: *${serviceName}*
Task Definition: ${revisionDisplay}
New Task Definition ARN: ${newTaskDefArn.split('/').last()}
    """.stripIndent()
    
    slackNotify(successMessage)

  } catch (Exception e) {
    echo "${pfx} [FAILURE] Error: ${e.getMessage()}"
    currentBuild.result = 'UNSTABLE'
    
    def revisionDisplay = taskDefRevision ?: 'latest'
    def errorMessage = """
*Deployment Failed*
Environment: *${envName}*
Service: *${serviceName}*
Task Definition: ${revisionDisplay}
Error: ${e.getMessage()}
    """.stripIndent()
    
    slackNotify(errorMessage)
  }
}

def slackNotify(String message) {
  try {
    withCredentials([string(credentialsId: 'slack-token', variable: 'SLACK_TOKEN')]) {
      withEnv(["SLACK_TOKEN=${SLACK_TOKEN}"]) {
        def escapedMessage = message
          .replace('\\', '\\\\')
          .replace('"', '\\"')
          .replace('\n', '\\n')
          .replace('\r', '')
          .replace('\t', ' ')
        
        sh """
            curl -X POST \\
            -H "Authorization: Bearer ${SLACK_TOKEN}" \\
            -H "Content-Type: application/json; charset=utf-8" \\
            -d '{"username":"Jenkins_Bot","channel":"mss-devops-deployment","text":"${escapedMessage}"}' \\
            https://slack.com/api/chat.postMessage
        """
      }
    }
  } catch (Exception e) {
    echo "[WARNING] Failed to send Slack notification: ${e.getMessage()}"
  }
}
''')
      sandbox(true)
    }
  }
}