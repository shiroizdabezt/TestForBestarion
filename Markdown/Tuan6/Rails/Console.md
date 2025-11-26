## Console
- Đây là môi tường tương tác giúp:
  - Chạy code ngay trong context của ứng dụng rails
  - Truy cập model, xử lý dữ liệu, chạy query
  - Debug, test
  - Kiểm tra db, kết nối, config
  - Chạy background jobs, mailer
  - Thực hiện tác vụ mà không cần viết code
- Ta có thể mở console cho từng db khác nhau. `rails console` sẽ mặc định mở môi trường `developer` nếu muốn mở một môi trường khác ta chỉ cần thêm tên của môi trường đó vào sau cùng dòng lệnh `rails console productions`
- Các thao tác thường dùng:
  - Tạo record: `User.create(name: "abc")`
  - Tìm record: `User.first`, `User.find`, `User.find(10)`, `User.where(active: true)`
  - Cập nhật thay đổi: `u = User.first => u.update(name: "New name")`
  - Xóa: `User.last.destroy`
  - Đếm: `User.count`
- Ngoài ra ta còn có thể có các thao tác khác liên quan đến db như
  - Xem danh sách bảng: `ActiveRecord::Base.connection.tables`
  - Xem cấu trúc bảng: `User.columns.map(&:name)`
  - Chạy lệnh SQL trực tiếp: `ActiveRecord::Base.connection.execute("SELECT * FROM users LIMIT 5")`
- Debug logic trong model/service:
```ruby
User.calculate_age(25)
Order.total_revenue
Product.search("coffee")
```
- Test mailers: `UserMailer.welcome_email(User.first).deliver_now`
- Tổng hợp một số lệnh query hay sử dụng trong console

| Mục đích        | Lệnh                             |
| --------------- | -------------------------------- |
| Lấy tất cả      | `User.all`                       |
| Lọc             | `User.where(active: true)`       |
| Tìm một record  | `User.find(1)`                   |
| Join            | `Order.joins(:customer)`         |
| Số lượng        | `User.count`                     |
| Sắp xếp         | `User.order(:name)`              |
| Nhiều điều kiện | `User.where.not(role: "admin")`  |
| Nâng cao        | `.group`, `.having`, `.includes` |
