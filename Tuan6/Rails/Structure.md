# Cấu trúc Thư Mục và Tập Tin trong Ứng Dụng Rails

Mỗi thư mục con và tệp trong thư mục ứng dụng đều được tạo ra với một mục đích cụ thể. Dưới đây là giải thích chi tiết về từng thư mục và tệp.

---

## Thư mục chính

| Thư mục | Mục đích |
|----------|----------|
| **app/** | Tổ chức các thành phần của ứng dụng. Bao gồm các thư mục con chứa phần hiển thị (views và helpers), bộ điều khiển (controllers), và logic xử lý phía backend (models). |
| **bin/** | Chứa các tập lệnh (scripts) để khởi động ứng dụng và có thể chứa các script khác dùng để thiết lập, cập nhật, triển khai, hoặc chạy ứng dụng. |
| **config/** | Chứa các tệp cấu hình cho ứng dụng, môi trường, routes, và các cài đặt liên quan. |
| **db/** | Dùng để quản lý cơ sở dữ liệu quan hệ. Bạn có thể tạo và đặt các script để quản lý database tại đây (như migration, schema, seed, v.v.). |
| **lib/** | Dành cho các thư viện do bạn viết thêm, trừ khi chúng thuộc về nơi khác (ví dụ thư viện bên thứ ba thì để trong `vendor/`). |
| **log/** | Chứa các file ghi log lỗi và hoạt động của ứng dụng. Rails tạo sẵn các tệp log cho từng môi trường: `development.log`, `test.log`, `production.log`, và log của server (`server.log`). |
| **public/** | Chứa các tệp tĩnh của trang web (không thay đổi), như JavaScript (`public/javascripts`), hình ảnh (`public/images`), stylesheet (`public/stylesheets`), và các tệp HTML. |
| **script/** | Chứa các script để khởi chạy và quản lý các công cụ khác nhau trong Rails, ví dụ như script để tạo code (`generate`) hoặc khởi chạy web server (`server`). |
| **storage/** | Chứa cơ sở dữ liệu SQLite và các tệp lưu trữ cho **Active Storage Disk Service**. (Được mô tả trong phần Active Storage Overview). |
| **test/** | Chứa toàn bộ các bài kiểm thử (test) bạn viết hoặc Rails tạo sẵn. Gồm các thư mục con: `mocks`, `unit`, `fixtures`, `functional`. |
| **tmp/** | Dùng để chứa các tệp tạm thời trong quá trình xử lý (như cache và pid files). |
| **vendor/** | Chứa các thư viện của bên thứ ba (third-party libraries), ví dụ như các gem được “vendored”. |


---

## Các tệp quan trọng trong thư mục ứng dụng

| Tệp | Mục đích |
|------|----------|
| **config.ru** | Cấu hình Rack để khởi động ứng dụng trên các máy chủ dựa trên Rack. |
| **Gemfile** / **Gemfile.lock** | Xác định các gem (thư viện Ruby) cần thiết cho ứng dụng. Được sử dụng bởi gem Bundler để cài đặt và quản lý dependency. |
| **Rakefile** | Định nghĩa các tác vụ (tasks) có thể chạy từ dòng lệnh. Thay vì sửa trực tiếp file này, bạn nên thêm các tác vụ mới trong `lib/tasks/`. |
| **README.md** | Tệp hướng dẫn ngắn gọn về ứng dụng. Bạn nên chỉnh sửa để mô tả chức năng, cách cài đặt, và hướng dẫn sử dụng. |
| **.rubocop.yml** | Cấu hình cho **RuboCop** – công cụ kiểm tra chuẩn mã nguồn Ruby. |
| **.ruby-version** | Chỉ định phiên bản Ruby mặc định của dự án. |

---

## Thư mục con trong `app/`

| Thư mục | Mục đích |
|----------|----------|
| **app/controllers/** | Chứa các lớp controller. Controller xử lý các yêu cầu web từ người dùng. |
| **app/helpers/** | Chứa các lớp helper hỗ trợ cho model, view, và controller, giúp mã ngắn gọn, dễ hiểu, và sạch sẽ hơn. |
| **app/models/** | Chứa các lớp model – biểu diễn và thao tác với dữ liệu trong cơ sở dữ liệu của ứng dụng. |
| **app/views/** | Chứa các template hiển thị, được render ra HTML và trả về trình duyệt người dùng. |
| **app/views/layouts/** | Chứa các template layout được dùng để bao bọc các view (như header/footer chung). Trong các view, bạn có thể định nghĩa layout bằng cú pháp `<tt>layout: "default"</tt>` và tạo file `default.html.erb`. Bên trong `default.html.erb`, dùng `<% yield %>` để render nội dung của view. |

---

**Tóm lại:**  
Cấu trúc thư mục trong ứng dụng Rails được thiết kế rất có tổ chức, giúp tách biệt rõ ràng giữa các phần của ứng dụng (Model – View – Controller), các file cấu hình, dữ liệu, thư viện, và script hỗ trợ. Việc nắm rõ cấu trúc này là bước nền tảng để phát triển và mở rộng ứng dụng Rails một cách hiệu quả.
