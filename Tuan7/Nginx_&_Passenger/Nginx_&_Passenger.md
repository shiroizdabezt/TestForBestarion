# Vai trò của Nginx và Passenger
## Nginx
- Nginx đóng vai trò web server ở tầng ngoài cùng nơi mà request HTTP/HTPPS từ client đi vào đầu tiên

### Vai trò chính
- Nhận HTTP/HTTPS request từ trình duyệt
  - Đây là cổng vào đầu tiên của ứng dụng 
  - Nginx lắng nghe port 80/443
- SSL termination
  - Nếu dùng HTTPS, Nginx sẽ giải mã SSL sau đó gửi request dạng HTTP cho Passenger
- Phục vụ static assets
  - Các file trong `/public` của rails được Nginx phục vụ trực tiếp
  - Điều này giảm tải cho Ruby
- Định tuyến request động cho Passenger
  - Mọi request không phải stitc thì sẽ chuyển vào Passenger
  - Nginx không hiểu ruby, chỉ biết chuyển request cho Passenger xử lý

## Passenger
- Passenger là application server cho Rails
  
### Vau trò chính
- Quản lý process của ứng dụng Rails
  - Passenger tạo và quản lý worker Rails
  - Các worker này xử lý request Ruby/Rails
- Tự động spawn/restart Rails
  - Khi có request đầu tiên Pasenger sẽ tự động boot Rails
  - Khi deploy chỉ cần tạo `tmp/restart.txt` Passenger sẽ restart worker một cách an toàn
- Tự động scale số lượng process Rails
  - Khi có nhiều load Passenger sẽ tăng số worker
  - Khi rảnh sẽ giảm bớt để tiết kiệm RAM
- Forward request vào Rails stack
  - Passenger nhận request đã xử lý sơ bộ từ Nginx sau đó đưa vào: Rack -> Rails Middleware -> Controller -> Model -> Render -> trả HTML/JSON -> Passenger -> Nginx -> client
- Giám sát crash 
  - Worker chết -> Passenger tự khởi động lại -> Đảm bảo tính sẵn sàng cho Rails
- Quản lí memory
  - Passenger có hệ thống theo dõi memory usage của worker Ruby
  - Có thể giới hạn RAM cho mỗi worker để tránh OOM

## Về cách Rails và Passenger, Nginx, Capistrano tương tác với nhau
- Framework Ruby on Rails cung cấp một công cụ máy chủ tích hợp sẵn mà ta có thể truy cập bằng lệnh `rails server`
- Tuy vậy lệnh này bản chất không phải là một máy chủ ứng dụng mà chỉ là một wrapper nhỏ khởi chạy ứng dụng của bạn bên tỏng một máy chủ ứng dụng thực tế
- Nên thường người dùng sẽ khoogn sử dụng nó trong môi trường production. Thay vào đó người dùng sử dụng một máy chủ ứng dụng như Passenger
- Kể từ Rails 5 `rails server` đã sử dụng Puma làm mặc định
- Nếu thêm Passenger vào `Gemfile` thì lệnh `rails server` sẽ khởi chạy Passenger thay vì Puma

### Passenger fits in the stack
- Trong một stack ta có thể sử dụng:
  - Nginx hoặc Apache để làm web server
  - Sử dụng Passenger làm application server
  - Capistrano để làm công cụ tự động hóa release

### Nginx và Apache
- Chúng sẽ cung cáp khả năng xử lý giao dịch HTTP và phục vụ các tệp tĩnh
- Tuy nhiên chúng không phải là máy chủ ứng dung Ruby và không thể chạy các ứng dụng Ruby trực tiếp
- Đó là lý do sử dụng Passenger kết hợp với chúng

### Capistrano
- Khi release phiên bản mới của ứng dụng web, có nhiều hành động cần được thực hiện, Capistrano cho phép tự động hóa các hành động này
- Capistrano không phải là một máy chủ cung cấp khả năng xử lý giao dịch HTTP vì vậy nó không thay thế được web server hoặc application server

### Những Điều Passenger Không Làm
Passenger không thực hiện các tác vụ sau (chúng nằm ngoài phạm vi):
- Thiết lập máy chủ với hệ điều hành: Passenger giả định rằng bạn đã có một máy chủ với một hệ điều hành đang hoạt động. Passenger là phần mềm để cài đặt, không phải dịch vụ hosting.
- Cài đặt Ruby: Để chạy ứng dụng Ruby, ta phải tự cài đặt Ruby trước. Ta chỉ cần cho Passenger biết vị trí của Ruby.
- Chuyển mã và tệp ứng dụng lên máy chủ: Passenger không tự chuyển mã và tệp. Ta nên sử dụng các công cụ như Capistrano để làm việc này. Passenger giả định rằng mã đã có sẵn trên máy chủ.
- Cài đặt các dependency của ứng dụng: Việc này thuộc về Bundler và Capistrano.
- Quản lý cơ sở dữ liệu: Nếu ứng dụng của ta yêu cầu cơ sở dữ liệu, Passenger không cài đặt, thiết lập tài khoản hoặc bảng. Chúng phải được thiết lập sẵn trước khi ta triển khai ứng dụng.