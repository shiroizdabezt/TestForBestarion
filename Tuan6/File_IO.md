## File I/O
- Ruby cũng hỗ trợ các thao tác đọc/ghi/tạo/xóa/kiểm tra file trên hệ thống bằng lớp File và IO
- Thao tác an toàn ta có thể sử dụng block File.open (nó sẽ tự động `close`)
- File I/O cũng bao gồm các chủ đề như `puts`, `gets`, `putc`, `print`
**Các phương thức trong File I/O**
1. puts
2. gets
3. putc
4. print

```ruby
# puts
puts "Hello, World!"
puts "This is Ruby."

# gets
puts "Enter your name:"
name = gets.chomp
puts "Hello, #{name}!"

# putc
putc "H"
putc "i"
    # Output: Hi

# print
print "Hello, "
print "World!"
    # Output: Hello, World!
```

**Thao tác với File**
- `File.new`: Tạo thêm một đối tượng tệp mới. Có thể chỉ định chế độ mở tệp như đọc (`r`), ghi (`w`), hoặc thêm (`a`)
```ruby
file = File.new("example.txt", "w")
file.puts "Hello, World!"
file.close
```
- `File.open`: Tương tự như `File.new`, nhnwg có thể được sử dụng với một khối lệnh. Khi sử dụng với khối lệnh, tệp sẽ tự động được đóng sau khi khối lệnh kết thúc 
```ruby
File.open("example.txt", "w") do |file|
  file.puts "Hello, World!"
end
```
- `sysread`: Đọc một số byte cụ thể từ tệp và trả về chúng dưới dạng chuỗi
```ruby
file = File.open("example.txt", "r")
content = file.sysread(20)
puts content
file.close
```
- `syswrite`: Ghi một chuỗi vào tệp
```ruby
file = File.open("example.txt", "w")
file.syswrite("Hello, World!")
file.close
```
- `each_byte`: Lặp qua từng byte trong tệp và thực hiện một khối lệnh cho mỗi byte
```ruby
File.open("example.txt", "r") do |file|
  file.each_byte { |byte| puts byte }
end
```
- `IO.readlines`: Đọc toàn bộ tệp và trả về một mảng các dòng
```ruby
lines = IO.readlines("example.txt")
lines.each { |line| puts line }
```
- `IO.foreach`: Đọc từng dòng trong tệp và thực hiện một khối lệnh cho mỗi dòng
```ruby
IO.foreach("example.txt") { |line| puts line }
```
- `File.rename`: Đổi tên tệp
```ruby
File.rename("example.txt", "new_example.txt")
```
- `File.delete`: Xóa tệp
```ruby
File.delete("new_example.txt")
```

**Các mode và quyền trong File**
| Mode       | Ý nghĩa                                                      |
| ---------- | ------------------------------------------------------------ |
| `"r"`      | read-only (mặc định)                                         |
| `"r+"`     | read/write, file phải tồn tại                                |
| `"w"`      | write — tạo mới hoặc ghi đè file                             |
| `"w+"`     | read/write — tạo mới hoặc ghi đè                             |
| `"a"`      | append (ghi nối cuối) — tạo nếu chưa có                      |
| `"a+"`     | read/append                                                  |
| Thêm `"b"` | binary mode (vd `"rb"`, `"wb"`)                              |
| Encoding   | có thể chỉ định encoding `"r:UTF-8"`, `"r:ISO-8859-1:UTF-8"` |

**Các cách kiểm tra thông tin về File**
- `File.exist?("filename")`: Kiểm tra xem tệp có tồn tại hay không
- `File.size("filename")`: Trả về kích thước của tệp
- `File.readable?("filename")`: Kiểm tra xem tệp có thể được đọc hay không
- `File.writeable?("filename")`: Kiểm tra xem tệp có thể được ghi hay không
- `File.executable?("filename")`: Kiểm tra xem tệp có thể được thực thi hay không

**Directory**
- `Dir.chdir`: Thay đổi thư mục làm việc hiện tại
```ruby
Dir.chdir("/path/to/directory")
puts Dir.pwd  # In ra thư mục hiện tại
```
- `Dir.mkdir`: Tạo thư mục mới
```ruby
Dir.mkdir("new_directory")
```
- `Dir.rmdir`: Xóa thư mục nhưng thư mục phải rỗng
```ruby
Dir.rmdir("new_directory")
```
- `Dir.entries`: Trả về một mảng chứa tên các tệp và thư mục trong thư mục hiện tại
```ruby
entries = Dir.entries(".")
puts entries.inspect
```
- `tempfile`: Thư viện để tạo tệp tạm thời
```ruby
require 'tempfile'

tempfile = Tempfile.new('my_temp_file')
tempfile.puts "This is a temporary file."
tempfile.close
```
- `tmpdir`: Thư viện tạo thư mục tạm thời