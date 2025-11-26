# config/deploy/production.rb

# Dùng code từ nhánh main
set :branch, "main"
set :rails_env, "production"

# Server Production
# ssh_options: fix lỗi fingerprint khi kết nối lần đầu
server "192.168.122.143", user: "ubuntu", roles: %w{app db web}, ssh_options: {
  verify_host_key: :never 
}
