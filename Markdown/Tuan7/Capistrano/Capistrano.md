# Capistrano 
- Là một công cụ tự động hóa. Giúp tự dodojg thực hiện câu lệnh trên 1 remote server bằng cách sử dụng SSH.
- Capistrano cần tối thiểu
  - Một Capistrano script, trong đó bao gồm những câu lệnh Capistrano phải thực hiện, cũng như server nào cần thực hiện. File này sử dụng ruby để viết
  - Một hoặc một vài configuration file để cung cấp thông tin các server, cũng như cách đăng nhập vào các server đó
- Capistrano có thể chạy các câu lệnh đồng thời trên các server khác nhau

## Workflow
- Cài đặt và chỉnh sửa Capistrano trên máy controller
- Mỗi khi cần deploy một version mới của ứng dụng, ta cần phải:
  - Commit và push code lên git repo mà ta đã cung cấp trong Capistrano
  - Chạy lệnh deploy của Capistrano

## Recipes
- Capistrano cũng có các recripes mà cộng đồng xây dựng
- Ta có thể sử dụng `capistrano/deploy` recipe trong đó chứa các câu lệnh giúp ta tự động `git clone`, `git pull`, cũng như hỗ trợ việc rollback rất dễ dàng.
- Hoặc nếu muốn deploy một ứng dung rails, `capistrano-rails` sẽ giúp ta chạy câu lệnh `bundle install`, compile Rails assets, chạy db migrate, ...

## Structure 
- Cấu trúc thư mục của Capistrano
```
/home/deploy/todo_app
├── releases/
│   ├── 20251118100000/  (Bản deploy cũ)
│   ├── 20251118100500/  (Bản deploy mới nhất)
│   └── ...
│
├── shared/
│   ├── config/
│   │   └── database.yml  (File liên kết, không bị ghi đè)
│   ├── log/
│   │   └── production.log  (File log liên kết)
│   ├── tmp/
│   ├── vendor/bundle/
│   └── public/uploads/
│
├── current -> /home/deploy/todo_app/releases/20251118100500/
│
└── repo/
    └── (Một bản sao 'bare' của repo Git của bạn)
```
- Các thư mục chính:
  - `release/`: 
    - Đây là nơi Capistrano lưu code của mỗi lần deploy
    - Mỗi thư mục con là một phiên bản hoàn chỉnh của code, đặt tên theo timestamp
    - Khi deploy, nó tạo một thư mục mới, kéo code mới vào, chạy `bundle install`, `assets:precompile`, ... ngay trong thư mục này
  - `shared/`: 
    - Đây là thư mục quan trọng nhất, nó chứa tất cả các file và thư mục mà ta muốn giữ lại giữa các lần deploy
    - Trong file `config/deploy.rb` ta có thể định nghĩa
      - `set :linked_files, %w{config/database.yml}`: điều này bảo Capistrano hãy lấy file `database.yml` trong `shared/config/` và tạo một lối tắt (symlink) đến nó trong thư mục `release` mới
      - `set :linked_dirs, %w{log tmp/pids ...}`: tương tự trên, bảo Capistrano hãy tạo lối tắt cho toàn bộ thư mục `log`, `tmp`, ...
    - Đây là lý do tại sao file log, file database config và các file user upload (`public/uploads`) của ta không bao giờ bị mắt sau mỗi lần deploy
  - `current/`:
    - Đây không phải là một thư mục thật, nó chỉ là một lối tắt `symbolic link`
    - Nó luôn trỏ đến phiên bản mới nhất và thành công nhất trong thư mục `releases/`
  - `repo/`
    - Capistrano giữ một bản sao "bare" của git repo ở đây. Khi deploy nó chỉ cần `git fetch` vào đây thay vì clone lại toàn bộ từ đầu, giúp tiết kiệm thời gian
  
## Các bước thực hiện (Cách 1: Nhúng thẳng Capistrano vào Gemfile)
### Cấu cấu hình Capistrano trên máy controller
**1. Cấu hình Gemfile** 
- Tại đây ta sẽ thêm các bộ công cụ của Capistrano mong muốn cài đặt
```ruby
group :development do
  # Bộ công cụ Capistrano
  gem "capistrano", "~> 3.17"
  gem "capistrano-rails", "~> 1.6"
  gem "capistrano-passenger", "~> 0.2.0"
  gem "capistrano-rvm"

    # --- QUAN TRỌNG: BẮT BUỘC NẾU DÙNG SSH KEY LOẠI ED25519 ---
  gem "ed25519", ">= 1.2", "< 2.0"
  gem "bcrypt_pbkdf", ">= 1.0", "< 2.0"
end
```
- Sau khi đã thêm các bộ công cụ trên ta tiến hành chạy `bundle install` để cài đặt

**2. Kích hoạt modules trong `Capfile`**
- Sau khi đã cài đặt các gói cài đặt trên, ta tiến hành chạy tiếp `bundle exec cap install` để có các file cấu hình của Capistrano
- Cài đặt xong ta sẽ được một file gọi là `Capfile`, đây là nơi để điều khiển, bật tắt các tính năng của Capistrano
```ruby
# Load DSL and set up stages
require "capistrano/setup"
require "capistrano/deploy"

# Include rvm, rails and passenger modules
require "capistrano/rvm"
require "capistrano/rails/assets"
require "capistrano/rails/migrations"
require "capistrano/bundler"
require "capistrano/passenger"
require "capistrano/scm/git"

install_plugin Capistrano::SCM::Git
```
- `require "capistrano/setup`: Nạp các thiết lập cơ bản và định nghĩa các môi trường
- `require "capistrano/deploy`: Nạp quy trình deploy cốt lõi
- `require "capistrano/rvm`: Giúp Capistrano nhận biết và sử dụng đúng phiên bản Ruby (RVM) trên server
- `require "capistrano/bundle"`: Dòng này cực kì quan trọng vì nó giúp chạy lệnh `bundle install` trên server để cài đặt thư viện còn thiếu. Nếu không có thì sẽ lỗi `Bundler::GemNotFound`
- `require "capistrano/rails/assets"`: Tự động chạy `rake assets:precompile` để nén CSS/JS cho môi trường Production
- `requie "capistrano/rails/migrations"`: Tự động chạy 'rake db:migrate` để cập nhật cấu trúc database nếu có thay đổi
- `require "capistrano/passenger"`: Tự động khởi động lại Passenger sau khi deploy xong
- `require "capistrano/scm/git"`: Sử dụng Git để tải mã nguồn từ Github/Gitlab về server
- `install_plugin Capistrano::SCM::Git`: Cài đặt Git làm công cụ quản lý mã nguôn chính

**3. Cấu hình tại `config/deploy.rb`**
- Đây là file quy định cách thức deploy của ứng dụng
```ruby
# config/deploy.rb

lock "~> 3.19.2"

set :application, "todo_app"
set :repo_url, "https://github.com/shiroizdabezt/Capistrano-todo-app-Rails.git" 

# Default branch is :master
# ask :branch, `git rev-parse --abbrev-ref HEAD`.chomp
set :branch, "main"

# Đường dẫn trên server (Sẽ tạo folder này sau)
set :deploy_to, "/var/www/todo_app"

# Cấu hình RVM
set :rvm_type, :system                     # Hoặc :system tuỳ vào cách bạn cài RVM
set :rvm_custom_path, '/usr/share/rvm'
set :rvm_ruby_version, '3.1.4'           # Phiên bản Ruby trên Server

# Các file và thư mục cần giữ lại giữa các lần deploy (Symlink)
append :linked_files, "config/database.yml", "config/master.key"
append :linked_dirs, "log", "tmp/pids", "tmp/cache", "tmp/sockets", "public/system", "storage"

# Số lượng bản release giữ lại
set :keep_releases, 5

# Khởi động lại Passenger sau khi deploy
set :passenger_restart_with_touch, true
```
- `lock "~> 3.19.2"`: Nên lock lại phiên bản để tránh gây xung đột khi có nhiều người sử dụng
- `set :application, "todo_app"`: Đặt tên ứng dụng
- `set :repo_url, ""`: Gán repo github nơi sẽ lấy code để chạy
- `set :branch, "main"`: Vì mặc định của Capistrano sẽ lấy nhánh `master` nên muốn sử dụng nhánh khác thì ta phải set cho nó
- `set :deploy_to, "/var/www/todo_app"`: Đường dẫn mà Capistrano sẽ tạo [cấu trúc thư mục](#structure) trên server. 
- `set :rvm_type, :system`: Ở đây có thể lựa chọn giữa `user` và `system`. Nếu là `user` Capistrano sẽ hiểu mặc định RVM được cài trong thư mục `home/ubuntu/.rvm` và ngược lại nó sẽ hiểu RVM được cài cho toàn bộ hệ thống ở `usr/local/rvm hoặc /usr/share/rvm`
- `set :rvm_custom_path, '/usr/share/rvm'`: Ngoài ra có thể gán cứng nơi mà mình muốn Capistrano sẽ nhận path của rvm bằng cách trên
- `set :rvm_ruby_version, '3.1.4'`: Phiên bản ruby trên server
- Vì mỗi lần deploy, Capistrano sẽ xóa code cũ và tải code mới về nên ta cần bảo vệ các file cấu hình như `database.yml` và `master.key` khỏi bị xóa bằng cách thêm chúng vào `linked_files` và các thư mục `linked_dirs`. Ta sẽ thêm bằng cách:
  - `append :linked_files, "config/database.yml", "config/master.key"`
  - `append :linked_dirs, "log", "tmp/pids", "tmp/cache", "tmp/sockets", "public/system", "storage"`
- `set :keep_releases, 5`: Số lượng bản release giữ lại sau khi deploy xong ở trong thư mục `release/`, nếu bị lỗi ta có thể rollback lại 1 trong 5 bản release gần nhất mà ta còn giữ trong `release/`
- `set :passenger_restart_with_touch, true`: Chạm vào file `/tmp/restart.txt` giúp Passenger khởi động lại mỗi khi deploy để giúp Passenger biết là có code mới và sẽ reload lại web

**4. Cấu hình Server đích tại `config/deploy/production.rb`**
- Đây sẽ là nơi khai báo nơi mà ta muốn Capistrano deploy đến, có IP nào, sử dụng user nào
```ruby
server "192.168.122.143", user: "ubuntu", roles: %w{app db web}, ssh_options: {
  verify_host_key: :never 
}
```
- `server "IP_ADDRESS", user: "USERNAME", roles: %w{...}`: 
  - `server "IP_ADDRESS"`: IP mà Capistrano sẽ gọi tới để deploy
  - `user: "USERNAME"`: Username mà Capistrano sẽ sử dụng trong toàn bộ quá trình deploy trên máy server
  - `roles`: 
    - `app`: Server này chạy mã nguồn Rails
    - `db`: Server này sẽ chạy lệnh migrate database
    - `web`: Server này sẽ chạy Nginx/Apache
  - `ssh_options`: Ở đây chỉ sử dụng option `verfify_host_key` để có thể skup phần bảo mật khi SSH vào không cần chọn yes

### Setup trên máy server
**1. Tạo thư mục, copy key**
- Đầu tiên ta cần có thư mục để Capistrano thực thi
```bash
sudo mkdir -p /var/www/todo_app
sudo chown -R ubuntu:ubuntu /var/www/todo_app
```
- Và Rails bản mới ta cần phải có `master.key` và `database.yml` để có thể thực thi
```bash
scp config/master.key ubuntu@192.168.122.143:/var/www/todo_app/shared/config/master.key
scp config/database.yml ubuntu@192.168.122.143:/var/www/todo_app/shared/
```
**2. Config Nginx**
- Tiếp đến ta sẽ cấu hình file config của Nginx để có thể đọc đến ứng dụng Rails
```conf
server {
    listen 8080;
    server_name 192.168.122.143;

    root /var/www/todo_app/current/public;

    passenger_enabled on;
    passenger_ruby /usr/share/rvm/gems/ruby-3.1.4/wrappers/ruby;
    passenger_app_env production;

    location ~ ^/(assets|packs)/ {
        gzip_static on;
        expires max;
        add_header Cache-Control public;
    }
}
```

### Lỗi đã gặp và cách khắc phục
- `Net::SSH::HostKeyMismatch`: Lỗi này xảy ra khi ta SSH vào máy server và máy server yêu cầu ta xác nhận lại thông tin của máy server
- `rvm exit status: 127`: Lỗi này xảy ra khi ta không có RVM được cài đặt trên máy server hoặc Capistrano tìm RVM ở `usr/local/rvm` (mặc định dùng `user`) thay vì `usr/share/rvm` (mặc định dùng `system`)
- `Bundler::GemNotFound`: Lỗi này xảy ra khi ta không có gem `capistrano` được cài đặt trên máy server ta cần thêm `require "capistrano/bundler"` vào file `Capfile`.
- 
## Các bước thực hiện (Cách 2: Capistrano Standalone)
- Muốn thực hiện Capistrano standalone ta phải `bundle init` để tạo ra một project dành riêng cho Capistrano
- Tiếp đến ta chạy lệnh `bundle add capistrano` để thêm gem capistrano vào trong project hoặc ta tự thêm các gem cần thiết vào trong file `Gemfile`
```ruby
source "https://rubygems.org"

gem "capistrano", "~> 3.19"
gem "capistrano-rails", "~> 1.6"
gem "capistrano-passenger", "~> 0.2"
gem "capistrano-rvm", "~> 0.1"
gem "capistrano-bundler", "~> 2.0"
gem "capistrano-yarn", "~> 0.1"
```
- Sau khi đã thêm các gem cần thiết ta chạy lệnh `bundle install` để cài đặt các gem
- Sau khi cài đặt xong ta chạy lệnh `bundle exec cap install` để cài đặt các file cấu hình cần thiết
- Sau khi cài đặt xong ta sẽ có các file cấu hình trong thư mục `config` và `lib`
- Tiếp đến ta tiến hành cấu hình Capistrano
- `ERR_SSL_PROTOCOL_ERROR`: Lỗi này xảy ra khi ta không có SSL được cài đặt trên máy server. Ta phải sửa `config/environments/production.rb`: `config.force_ssl = false`
**1. Cấu hình `Capfile`**
```ruby
# Load DSL and set up stages
require "capistrano/setup"

# Include default deployment tasks
require "capistrano/deploy"

# Include rvm, rails and passenger modules
require "capistrano/rvm"

require "capistrano/bundler"
require "capistrano/rails/assets"     # Precompile assets (CSS/JS)
require "capistrano/rails/migrations" # Chạy db:migrate
require "capistrano/passenger"
require "capistrano/scm/git"
require "whenever/capistrano"

install_plugin Capistrano::SCM::Git

# Load custom tasks from `lib/capistrano/tasks` if you have any defined
Dir.glob("lib/capistrano/tasks/*.rake").each { |r| import r }
```

**2. Cấu hình tại `config/deploy.rb`**
```ruby
# config/deploy.rb

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
```

**3. Cấu hình tại `config/deploy/production.rb`**
```ruby
server "192.168.122.143", user: "ubuntu", roles: %w{app db web}, ssh_options: {
  verify_host_key: :never 
}
```
**4. Cấu hình tại `config/deploy/staging.rb`**
```ruby
server "192.168.122.143", user: "ubuntu", roles: %w{app db web}, ssh_options: {
  verify_host_key: :never 
}
```
- Ta có thể viết thêm các task custom vào trong file `lib/capistrano/tasks/custom.rake` để có thể tái sử dụng các task trong quá trình deploy
```ruby
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
```
- Trong đó:
  - `namespace :custom do`: Tạo namespace custom
  - `desc "Thông báo bắt đầu deploy"`: Mô tả task
  - `task :notify_start do`: Tạo task notify_start
  - `run_locally do`: Chạy task trên máy local
  - `info "🚀 Bắt đầu deploy lên server..."`: In thông báo
  - `on roles(:web) do`: Chạy task trên server web
  - `within release_path do`: Chạy task trong thư mục release
  - `execute :rake, 'tmp:clear'`: Chạy lệnh rake trên server
  - `upload! "config/special_secret.json", "#{shared_path}/config/special_secret.json"`: Upload file cấu hình đặc biệt
- Ta có thể chèn các task custom vào trong chuỗi deploy 
```ruby
before 'deploy:starting', 'custom:notify_start'
before 'deploy:symlink:linked_files', 'custom:upload_secret'
after 'deploy:finished', 'custom:clear_cache'
```
- Trong đó:
  - `before 'deploy:starting', 'custom:notify_start'`: Chèn task notify_start vào chuỗi deploy trước khi bắt đầu deploy
  - `before 'deploy:symlink:linked_files', 'custom:upload_secret'`: Chèn task upload_secret vào chuỗi deploy trước khi tạo symlink
  - `after 'deploy:finished', 'custom:clear_cache'`: Chèn task clear_cache vào chuỗi deploy sau khi deploy xong
- Ta có thể ghi đè hành động restart mặc định
```ruby
after 'deploy:publishing', 'deploy:restart'
```
- Trong đó:
  - `after 'deploy:publishing', 'deploy:restart'`: Ghi đè hành động restart mặc định
- Ta có thể restart lại passenger sau khi deploy
```ruby
set :passenger_restart_with_touch, true
```
- Trong đó:
  - `set :passenger_restart_with_touch, true`: Restart lại passenger sau khi deploy
- Cuối cùng sau khi đã cấu hình xong ta có thể deploy lên server
```bash
bundle exec cap production deploy
# Hoặc
bundle exec cap staging deploy
```
