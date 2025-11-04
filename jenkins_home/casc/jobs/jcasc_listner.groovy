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
        }
      }
      scriptPath('jenkins_home/casc/Jenkinsfile')
      lightweight(true)
    }
  }

  triggers {
    GenericTrigger(
      genericVariables: [
        [key: 'ADDED',    value: '$.commits[*].added[*]',    expressionType: 'JSONPath'],
        [key: 'MODIFIED', value: '$.commits[*].modified[*]', expressionType: 'JSONPath'],
        [key: 'REMOVED',  value: '$.commits[*].removed[*]',  expressionType: 'JSONPath'],
        [key: 'REF',      value: '$.ref',                     expressionType: 'JSONPath'] // refs/heads/main
      ],
      token: 'your-very-long-random-token',   // dùng trong URL webhook
      tokenCredentialId: '',                  // (tuỳ chọn) nếu muốn dùng secret từ Credentials
      printContributedVariables: true,        // bật log để debug
      printPostContent: false,                // bật true nếu muốn xem full payload (cân nhắc log)
      // Chuỗi dữ liệu đem đi lọc regex
      regexpFilterText: '$ADDED,$MODIFIED,$REMOVED|$REF',
      // Chỉ TRIGGER nếu có file trong thư mục cần theo dõi (vd jenkins_home/casc/)
      // + tuỳ chọn: chỉ trên nhánh main
      // Giải thích: phần trước dấu | là danh sách file; sau | là ref
      regexpFilterExpression: '(?s).*(?:^|,)(?:jenkins_home/casc/)[^,]+.*\\|refs/heads/main'
    )
  }

  properties {
    disableConcurrentBuilds()
  }
}
