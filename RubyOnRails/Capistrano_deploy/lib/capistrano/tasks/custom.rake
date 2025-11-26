namespace :custom do
  desc "Thông báo bắt đầu deploy"
  task :notify_start do
    run_locally do
      info "🚀 Bắt đầu deploy lên server..."
      # Ví dụ: Gửi thông báo Slack/Discord
      # execute "curl -X POST -d 'payload=...' [https://hooks.slack.com/](https://hooks.slack.com/)..."
    end
  end

  desc "Xóa cache của Rails"
  task :clear_cache do
    on roles(:web) do
      within release_path do
        # Chạy lệnh rake trên server (trong thư mục release mới nhất)
        execute :rake, 'tmp:clear'
      end
    end
  end
  
  desc "Upload file cấu hình đặc biệt (không nằm trong git)"
  task :upload_secret do
    on roles(:app) do
      # Upload từ máy local lên server
      upload! "config/special_secret.json", "#{shared_path}/config/special_secret.json"
    end
  end
end