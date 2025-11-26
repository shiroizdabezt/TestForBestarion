# config valid for current version and patch releases of Capistrano
lock "~> 3.19.2"

set :application, "todo_app"
set :repo_url, "https://github.com/shiroizdabezt/Capistrano-todo-app-Rails.git"

# Đường dẫn deploy trên server
set :deploy_to, "/var/www/todo_app"

# Cấu hình RVM (Server dùng System RVM tại /usr/share/rvm)
set :rvm_type, :system
set :rvm_custom_path, '/usr/share/rvm'
set :rvm_ruby_version, '3.1.4'

# Các file và thư mục cần giữ lại giữa các lần deploy (Symlink)
append :linked_files, "config/database.yml", "config/master.key"
append :linked_dirs, "log", "tmp/pids", "tmp/cache", "tmp/sockets", "public/system", "storage"

# Số lượng bản release giữ lại
set :keep_releases, 5

# Khởi động lại Passenger sau khi deploy
set :passenger_restart_with_touch, true

# Thêm các task custom vào chuỗi deploy
before 'deploy:starting', 'custom:notify_start'

# 2. Upload file secret TRƯỚC KHI tạo symlink (để code mới có file mà dùng ngay)
before 'deploy:symlink:linked_files', 'custom:upload_secret'

# 3. Xóa cache SAU KHI restart xong web server
after 'deploy:finished', 'custom:clear_cache'

# Ví dụ Restart Systemd service (nếu dùng Passenger Standalone)
namespace :deploy do
  desc 'Restart application'
  task :restart do
    on roles(:app), in: :sequence, wait: 5 do
      # Lệnh restart systemd (cần visudo cho user ubuntu không pass)
      execute :sudo, :systemctl, :restart, :todo_app
    end
  end
end

# Ghi đè hành động restart mặc định
after 'deploy:publishing', 'deploy:restart'
