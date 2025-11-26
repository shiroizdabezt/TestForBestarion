## String
- Là một chuỗi các ký tự được bao trong dấu `' '` hoặc `" "`
- Là một đối tượng của lớp `String` trong Ruby
  
**Cách khai báo**
```ruby
a = "hello"
b = 'hi'
```
- Sự khác nhau giữa khai báo dấu `' '` và `" "`:
  - `' '`: Không xử lý được các ký tự đặc biệt hay nội suy
  - `" "`: Có thể dùng các ký tự đặc biệt và nội suy biến
| Ký tự | Ý nghĩa             |
| ----- | ------------------- |
| `\n`  | xuống dòng          |
| `\t`  | tab                 |
| `\"`  | dấu nháy kép        |
| `\\`  | dấu gạch chéo ngược |


```ruby
name = "Ruby"
puts 'Hello #{name}'   # => Hello #{name}
puts "Hello #{name}"   # => Hello Ruby
```
**Nối chuỗi**
```ruby
a = "Hello"
b = "World"

puts a + " " + b       # => "Hello World"
puts "#{a} #{b}"       # => "Hello World"
puts [a, b].join(" ")  # => "Hello World"
```

**Nội suy chuỗi**
Là cách Dùng " " hoặc %Q{} để chèn biến, biểu thức Ruby vào chuỗi
```ruby
name = "Ruby"
version = 3.3
puts "Hello #{name}, version #{version}"  
# => Hello Ruby, version 3.3

puts "2 + 3 = #{2 + 3}"  
# => 2 + 3 = 5
```

**Một số phương thức thông dụng**
| Phương thức       | Ý nghĩa                      | Ví dụ                                    |
| ----------------- | ---------------------------- | ---------------------------------------- |
| `length` / `size` | Độ dài chuỗi                 | `"Ruby".length #=> 4`                    |
| `upcase`          | Chuyển thành chữ hoa         | `"ruby".upcase #=> "RUBY"`               |
| `downcase`        | Chuyển thành chữ thường      | `"RUBY".downcase #=> "ruby"`             |
| `capitalize`      | Viết hoa ký tự đầu           | `"ruby".capitalize #=> "Ruby"`           |
| `reverse`         | Đảo ngược chuỗi              | `"abc".reverse #=> "cba"`                |
| `include?`        | Kiểm tra chứa chuỗi con      | `"hello".include?("he") #=> true`        |
| `gsub`            | Thay thế chuỗi               | `"hello".gsub("l", "x") #=> "hexxo"`     |
| `strip`           | Xóa khoảng trắng đầu và cuối | `" hello ".strip #=> "hello"`            |
| `split`           | Cắt chuỗi thành mảng         | `"a,b,c".split(",") #=> ["a", "b", "c"]` |
| `chomp`           | Xóa ký tự xuống dòng ở cuối  | `"hello\n".chomp #=> "hello"`            |
| `to_i`, `to_f`    | Chuyển chuỗi thành số        | `"123".to_i #=> 123`                     |


| Ký hiệu       | Loại                                 | Ví dụ                | Tương đương                |
| ------------- | ------------------------------------ | -------------------- | -------------------------- |
| `%q`          | Chuỗi **như nháy đơn** `' '`         | `%q{Ruby is fun.}`   | `'Ruby is fun.'`           |
| `%Q` hoặc `%` | Chuỗi **như nháy kép** `" "`         | `%Q{Ruby #{1+1}}`    | `"Ruby 2"`                 |
| `%r`          | Regular expression (Regex)           | `%r{\d+}`            | `/\d+/`                    |
| `%w`          | Mảng **chuỗi đơn**                   | `%w[red green blue]` | `["red", "green", "blue"]` |
| `%W`          | Mảng **chuỗi kép (có nội suy)**      | `%W[#{1} two three]` | `["1", "two", "three"]`    |
| `%i`          | Mảng **symbol đơn**                  | `%i[cat dog pig]`    | `[:cat, :dog, :pig]`       |
| `%I`          | Mảng **symbol kép (có nội suy)**     | `%I[#{'x'} y z]`     | `[:x, :y, :z]`             |
| `%s`          | Tạo **symbol**                       | `%s[Ruby!]`          | `:"Ruby!"`                 |
| `%x`          | Chạy **lệnh shell (system command)** | `%x!ls!`             | `` `ls` ``                 |

**Tạo chuỗi nhiều dòng**
```ruby
text = <<~DOC
  This is a 
  multiline string.
DOC
puts text
```