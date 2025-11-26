## Migration
- Migration là các file Ruby được dùng để mô tả thay đổi của schema của cơ sở dữ liệu
- Các file này nằm ở thư mục `db/migrate/`
- Những thao tác migration có thể làm 
  - `creat_table(name, options)`: Tạo bảng mới
  - `drop_table(name)`: Xóa bảng
  - `add_column(table_name, column_name, type, options)`: Thêm cột
  - `rename_column(table_name, column_name, new_column_name)`: Đổi tên cột
  - `change_column(table_name, column_name, type, options)`: Thay kiểu/tùy chọn cột
  - `remove_column(table_name, column_name)`: Xóa cột
  - `add_index(table_name, column_name, index_options)`: Thêm index
  - `remove_index(table_name, column_name): Xóa index
  - `add_reference`, `add_foreign_key`, `create_join_table`, v.v
- Các kiểu dữ liệu hỗ trợ trong migration
  - `string`: Chuỗi ngắn
  - `text`: Văn bản dài
  - `integer`: Số nguyên
  - `float`: Số thập phân
  - `demical`: Số chính xác với `precision`/`scale`
  - `datetime`, `timestamp`: Lưu ngày + giờ
  - `date`,`time`: Chỉ ngày hoặc chỉ giờ
  - `binary`: Dữ liệu nhị phân
  - `boolen`: True/False
- Options
  - `default: `: Giá trị mặc định
  - `null: `: Cho phép null hay không (true/false)
  - `limit: `: Giới hạn độ dài string hoặc bytes cho text/binary
  - `precision: ` và `scale: `: Cho `demical`
  - `comment: `: Comment cho cột
  - `collation: `: Collation cho string/text

### Cách tạo và sử dụng migration
```bash
rails generate model Book title:string price:float subject:references
rails generate model Subject name:string
```
- Sau khi tạo xong ta sẽ có file dạng như sau
```ruby
class CreateBooks < ActiveRecord::Migration[8.0]
  def change
    create_table :books do |t|
      t.string :title
      t.float :price
      t.references :subject, null: false, foreign_key: true
      t.timestamps
    end
  end
end

# Và

class CreateSubjects < ActiveRecord::Migration[8.0]
  def change
    create_table :subjects do |t|
      t.string :name
      t.timestamps
    end
  end
end
```
- Sau khi đã hoàn thành xong file migrate thì ta chỉ cần chạy lệnh `rails db:migrate` thì rails sẽ tiến hành apply các thay đổi đó vào môi trường ta chọn nếu không chọn thfi mặc định sẽ là developer
- Sau khi migrate thì rails sẽ thực hiện tạo/cập nhật file `db/schema.rb`
```ruby
ActiveRecord::Schema[8.0].define(version: 2025_03_05_182034) do
  create_table "books", force: :cascade do |t|
    t.string "title"
    t.float "price"
    t.integer "subject_id", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["subject_id"], name: "index_books_on_subject_id"
  end

  create_table "subjects", force: :cascade do |t|
    t.string "name"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  add_foreign_key "books", "subjects"
end
```