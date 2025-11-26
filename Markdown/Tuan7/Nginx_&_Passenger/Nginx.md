# Tìm hiểu về context Nginx config
- File config chính của Nginx thông thường sẽ nằm ở đường dẫn `/etc/nginx/nginx.conf` hoặc `/usr/local/nginx/conf/nginx.conf` hoặc `/usr/local/etc/nginx/nginx.conf`
## Start, Stop, Reloading
- Ta có thể điều khiển Nginx đang chạy bằng lệnh
```bash
nginx -s signal
```
- `signal` có thể là: 
  - `stop`: shutdown nhanh
  - `quit`: shutdown 
  - `reload`: reload các file config
  - `reopen`: mở lại các log file

## Cấu trúc file cấu hình Nginx

### Module và Directives
- Nginx bao gồm các `module`
- Mỗi `module` cung cấp một bộ chức năng cụ thể
- Các `module` này được điều khiển bở các `directive` được chỉ định trong file cấu hình
- `directives` được chia thành 2 loại:
  - Simple Directives: Bao gồm tên và các tham số dược phân tách bằng dấu cách và kết thúc bằng dấu `;`
    - `worker_processess 4;`
    - `root data/www;`
  - Block Directives: Cấu trúc tương tự như `Simple Directives` nhưng thay vì dấu `;` nso kết thúc bằng một tập hợp các hướng dẫn bổ sung được bao quanh bởi `{ }`
    - `server { listen 80; }`

### Context Chính
#### Main context
- Context khái quát nhất của Nginx. Đây là context duy nhất không nằm trong các khối context. Có dạng
```conf
context {

}
```
- Bất kỳ directives nào tồn tại bên ngoài các khối này đều sẽ được cho là `main context`. 
- Main context thể hiện một môi trường rộng nhất dành cho NGINX config. Nó được sử dụng để cấu hình các chi tiết ảnh hưởng đến toàn bộ ứng dụng ở cấp độ cơ bản.
- Một số chi tiết phổ biến được cấu hình trong main context là người dùng và nhóm để chạy worker processess. Như số lượng worker, file để lưu PID của main process, CPU worker. File error_log mặc định của toàn bộ ứng dụng có thể được thiết lập ở cấp độ này

#### Event context
- `Event context` là một context được chứa bên trong main context. Nó dùng để đặt các tùy chọn ở mức độ blogal, ảnh hưởng đến cách NGINX xử lý các kết nối ở cấp độ chung.
- `Event context` có dạng:
```conf
events {

}
```
- NGINX sử dụng mô hình xử lý kết nối dựa trên các event. Do đó, các directive được xác định trong context này sẽ xác định cách worker processes ở trên xử lý các kết nối.

#### HTTP context
- Khi cấu hình NGINX như một web server hay một reverse proxy, http context sẽ chiếm phần lớn cấu hình.
- Context này sẽ chứa mọi directive cũng như các context cần thiết khác để xác định cách các chương trình xử lý kết nối HTTP hay HTTPS.
- `HTTP context` có dạng
```conf
events {

}

http {

}
```
- Các context thấp hơn sẽ cụ thể hơn về cách xử lý request. Nhưng các directive ở cấp độ này sẽ kiểm soát mặc định cho mỗi máy chủ ảo được xác định trong nó.
- Một số directive thường gặp kiểm soát location mặc định cho các truy cập và nhật ký lỗi (`access_log` và `error_log`).
- Nó cũng cấu hình I/O không đồng bộ cho các hoạt động của file (`aio`, `sendfile`, `directio`).
- Cấu hình trạng thái server khi xảy ra lỗi (`error_page`).
- Một số directive khác cấu hình nén (`gzip`, `gzip_disable`), fine-tune cài đặt keep alive TCP (`keepalive_disable`, `keepalive_requests`, `keepalive_timeout`).
- cấu hình các quy tắc mà NGINX sẽ theo để tối ưu hóa các packet và system call (`sendfile`, `tcp_nodelay`, `tcp_nopush`).
-  Các directive bổ sung cấu hình root các tài liệu tầng ứng dụng và index file (`root`, `index`).
-  Nó cũng thiết lập các hash table dùng để chứa nhiều loại dữ liệu khác nhau (`*_hash_bucket_size` và `*_hash_max_size` cho `server_names`, `types`, `variables`).
-  

#### Server context
- Server Context được khai báo trong http context.
- Server context cũng là context đầu tiên cho phép khai báo nhiều lần.
- Server context có dạng như sau:
```conf
http {
  server {

  }

  server {

  }
}
```
- Trong context này, mỗi trường hợp sẽ xác định một virtual server cụ thể để xử lý yêu cầu của client.
- Các directive được sử dụng để quyết định server context là:
  - `listen`: là sự kết hợp địa chỉ IP/port mà server block này được thiết kế để respond. Nếu một request từ client phù hợp với các giá trị này, block này có khả năng sẽ được lựa chọn để xử lý kết nối.
  - `server_name`: directive này là một thành phần khác, dùng để chọn một server block để xử lý. Nếu có nhiều server đáp ứng được directive listen, NGINX sẽ phân tích cú pháp header “Host” của request và lựa chọn block phù hợp.

#### Location context
- Location được dùng để xử lý một loại client request xác định.
- Location block không giống với server block – lồng bên trong nhau, chúng sẽ nằm bên trong các server context.

```conf
server {

    location /match/criteria {

    }

    location /other/criteria {


        location nested_match {

        }

        location other_nested {

        }
    }
}
```