
# Dùng code từ nhánh develop
set :branch, "main"
set :rails_env, "staging"

# Server Staging (1 máy làm tất cả)
server "192.168.122.143", user: "ubuntu", roles: %w{app db web}, ssh_options: {
  verify_host_key: :never 
}

