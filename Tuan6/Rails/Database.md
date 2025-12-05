## Database
**Bước 1**: 
- Đàu tiên ta cầ phải kiểm tra trong file `Gemfile` để xác nhận xem đã cài đặt `gem 'pg'` cho rails hay chưa.
- Nếu chưa ta cần phải chạy lệnh ` `

**Bước 2**:
- Ta cần cấu hình file database trong `config/database.yml` 
```yml 
default: &default
  adapter: postgresql
  encoding: unicode
  # For details on connection pooling, see Rails configuration guide
  # https://guides.rubyonrails.org/configuring.html#database-pooling
  pool: <%= ENV.fetch("RAILS_MAX_THREADS") { 5 } %>


development:
  <<: *default
  database: testdb
  username: tester 
  password: 123
  host: localhost
  port: 6432
```

**Bước 3**:
- Ta cần phải kiểm tra xem đã có thể vào được db hay chưa bằng cách
```bash
bin/rails db

#hoăc

bin/rails dbconsole

#hoặc ta có thể vào console 
bin/rails console
> ActiveRecord::Base.connection.tables
```
**Bước 4**: 
- Nếu db đã có sẵn schema thì ta chạy lệnh sau để dump schema hiện có vào `db/schema.rb`:
```bash
bin/rails db:schema:dump
```

**Bước 5**:
- Dù đã có các bảng hay schema tuy nhiên khi kết nối vào bằng rails thì rails cũng không tự tạo các model để thao tác vào các bảng đó
- Vì vậy ta cần phải tạo model để có thể thao tác vào các bảng đó
- Có thể tạo model bằng lệnh hoặc tạo thủ công bằng file `rails generate model User --skip-migration` 
```ruby
# app/models/user.rb
class User < ApplicationRecord
  # nếu bảng tên không theo quy chuẩn (ví dụ: người đặt tên khác):
  # self.table_name = "tablename_in_db"
  # nếu khóa chính khác:
  # self.primary_key = "id_column_name"
end
```
- Nếu bảng có tên `users` thì `class User < ApplicationRecord` sẽ tự map đúng.

**Bước 6**:
- Khi muốn thay đổi db bằng rails, ta sẽ tạo migration và thay đổi trong đó
```bash
rails generate migration AddAgeToUsers age:integer
# hoặc tạo table
rails generate migration CreateInvoices number:string total:decimal
```

**Chú ý**:
- Vì làm việc với db rất quan trọng nên cần phải cẩn thận
- Ta có thể sử dụng ``rails console --sandbox` để test các thay đổi xem có lỗi gì không trước khi thực sự thay đổi nó. Vì khi sử dụng sanbox thì thoát ra tất cả mọi thứ sẽ được rollback lại như khi ta vào
- Ngoài ra ta nên backup dữ liệu bằng `pg_dump` để tránh mất mát dữ liệu khi sử dụng db
