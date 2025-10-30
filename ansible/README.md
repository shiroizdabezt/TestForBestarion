# This folder contains ansible code
## Cách chạy
### Ansible Master
1. Check xem đã đúng host chưa trong folder `/invontory/hosts.yml`
2. Check các biến môi trường (sẽ bao gồm cả password của admin và một số token hoặc api cần thiết) trong `/group_vars/master/jenkins_env.yml`. Vì đây là file nhạy cảm nên sẽ bị mã hóa bằng `vault` của ansible, cần có password để có thể edit hoặc xem file này
3. Check đường dẫn nơi lưu các file config của JCasC ở `/group_vars/all/jenkins.yml`
4. Nếu tất cả đã đúng với mong muốn thì để chạy ta vào thư mục `ansible` và chạy lệnh `ansible-playbook playbooks_master/install_jenkins.yml -i inventory/hosts.yml` 

### Ansible Agent
1. Check xem đã đúng host chưa trong folder `/group_vars/all/jenkins.yml`
2. Check `Agent name`,`host Port` và `host IP` đã đúng hay chưa trong file `group_vars/agent/jenkins.yml` 
3. Nếu tất cả đã đúng với mong muốn thì để chạy ta vào thư mục `ansible` và chạy lệnh `ansible-playbook playbooks_agent/connect_inbound_agent_to_master.yml -i inventory/hosts.yml` 

### Những biến cần thay đổi khi muốn deploy trên aws và dùng những biến lưu trên đó
1. Các biến hoặc các password được lưu trên `/group_vars/master/jenkins_env.yml`. Nếu muốn sử dụng các biến trên aws thì các biến trong này không thể sử dụng được nữa mà phải thay thế bằng các biến được lưu trên aws. Các biến trong file trên được sử dụng tại `/roles/jenkins_master_role/tasks/envfile.env`. Có thể ghi đè lên các biến này bằng các giá trị được lưu trên aws
2. 