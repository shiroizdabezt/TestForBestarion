## Hash
- Trong ruby hash là một kiểu dữ liệu, cho phép lưu trữ và truy cập dữ liệu theo cặp key value.
- Mỗi key là duy nhất, value có thể trùng nhau

**Cách tạo hash**
1. Sử dụng `{ }`
2. Sử dụng `Hash.new`
3. Sử dụng `{ }` kiểu mới

```ruby
# Cách 1
hash = { "name" => "Alice", "age" => 30 }

# Cách 2
hash = Hash.new    # Tạo một Hash rỗng
hash = Hash.new(0) # Tạo một Hash với giá trị mặc định là 0

# Cách 3
hash = { name: "Alice", age: 30 }
```

**Truy cập các phần tử trong hash**
```ruby
hash = { "name" => "Alice", "age" => 30 }
puts hash["name"]  # Output: Alice
puts hash["age"]   # Output: 30
```

**Các phương thức cơ bản trong hash**
| Phương thức         | Mô tả                       | Ví dụ                  | Kết quả           |
| ------------------- | --------------------------- | ---------------------- | ----------------- |
| `keys`              | Trả về mảng tất cả key      | `{a:1,b:2}.keys`       | `[:a, :b]`        |
| `values`            | Trả về mảng tất cả value    | `{a:1,b:2}.values`     | `[1,2]`           |
| `length` / `size`   | Số cặp key–value            | `{a:1,b:2}.size`       | `2`               |
| `empty?`            | Kiểm tra rỗng               | `{}`.empty?            | `true`            |
| `has_key?` / `key?` | Kiểm tra tồn tại key        | `{a:1}.key?(:a)`       | `true`            |
| `has_value?`        | Kiểm tra tồn tại value      | `{a:1}.has_value?(1)`  | `true`            |
| `delete(key)`       | Xóa cặp theo key            | `{a:1,b:2}.delete(:a)` | `1`               |
| `clear`             | Xóa toàn bộ hash            | `{a:1,b:2}.clear`      | `{}`              |
| `merge`             | Gộp 2 hash                  | `{a:1}.merge(b:2)`     | `{:a=>1,:b=>2}`   |
| `invert`            | Đổi chỗ key ↔ value         | `{a:1,b:2}.invert`     | `{1=>:a,2=>:b}`   |
| `to_a`              | Chuyển thành mảng           | `{a:1,b:2}.to_a`       | `[[:a,1],[:b,2]]` |
| `to_h`              | Ngược lại – từ mảng về hash | `[[:a,1],[:b,2]].to_h` | `{:a=>1,:b=>2}`   |

**Các phương thức khác**
- `select`, `filter`: Tạo ra một Hash chứa các cặp khóa giá trị thỏa mãn một điều kiện nhất định
- `transform_keys`, `transform_values`: Tạo ra một Hash mới bằng cách áp dụng một khối lệnh cho các khóa hoặc giá trị trong Hash
- `slice`: Tạo ra một Hash mới chứa các cặp khóa-giá trị được chỉ định
- `compact`: Tạo ra một Hash mới bằng cách loại bỏ các cặp khóa giá trị có giá trị nil

```ruby
hash = { "name" => "Alice", "age" => 30, "city" => nil }

new_hash = hash.select { |key, value| value.is_a?(String) }
# new_hash is now {"name"=>"Alice"}

new_hash = hash.transform_keys { |key| key.to_s.upcase }
# new_hash is now {"NAME"=>"Alice", "AGE"=>30, "CITY"=>nil}

new_hash = hash.slice("name", "age")
# new_hash is now {"name"=>"Alice", "age"=>30}

new_hash = hash.compact
# new_hash is now {"name"=>"Alice", "age"=>30}
``` 
**Symbols làm khóa**
Trong ruby có thể sử dụng Symbols làm khóa cho Hash. Symbols là một kiểu dữ liệu đặc biệt trong Ruby, tương tự như String nhưng được sử dụng để đại diện cho các định danh bất biến.

```ruby
hash = { :name => "Alice", :age => 30 }
# hoặc
hash = { name: "Alice", age: 30 }

puts hash[:name]  # Output: Alice
puts hash[:age]   # Output: 30
```