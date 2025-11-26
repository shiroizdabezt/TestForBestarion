## Regex
- Regular Expression (biểu thức chính quy) là một mẫu (pattern) dùng để tìm kiếm, so khớp, hoặc thay thế chuỗi văn bản.
- Trong Ruby, Regex được định nghĩa bởi cặp dấu gạch chéo /pattern/ hoặc bằng hàm khởi tạo `Regexp.new`.

```ruby
regex1 = /hello/
regex2 = Regexp.new("hello")
```

**Các bộ điều chỉnh**
Bộ điều chỉnh (modifier) là các ký tự đặc biệt được thêm vào cuối biểu thức Regex để thay đổi cách nó hoạt động. Dưới đây là một số bộ điều chỉnh phổ biến trong Ruby:
`i`: Không phân biệt chữ hoa chữ thường.
`m`: Cho phép dấu chấm `.` khớp với ký tự xuống dòng.
`x`: Bỏ qua khoảng trắng và cho phép thêm chú thích trong Regex.
`o`: Thực hiện thay thế `#{...}` chỉ một lần.

```ruby
# Không phân biệt chữ hoa chữ thường
regex = /hello/i
puts "Hello".match?(regex)  # Output: true

# Cho phép dấu chấm khớp với ký tự xuống dòng
regex = /a.b/m
puts "a\nb".match?(regex)  # Output: true

# Bỏ qua khoảng trắng và cho phép thêm chú thích
regex = /
  a   # Ký tự a
  b   # Ký tự b
/x
puts "ab".match?(regex)  # Output: true
```

**Các mẫu biểu thức**
Các mẫu biểu thức Regex cho phép xác định các ký tự hoặc chuỗi ký tự cụ thể mà ta muốn tìm kiếm.
`.`: Bất kỳ ký tự nào ngoại trừ ký tự xuống dòng.
`\d`: Bất kỳ chữ số nào (0-9).
`\w`: Bất kỳ ký tự từ, chữ số hoặc dấu gạch dưới.
`\s`: Bất kỳ ký tự khoảng trắng nào.
`^`: Bắt đầu của chuỗi.
`$`: Kết thúc của chuỗi.
`[abc]`: Bất kỳ ký tự nào trong tập hợp a, b, hoặc c.
`[^abc]`: Bất kỳ ký tự nào không nằm trong tập hợp a, b, hoặc c.
`a|b`: a hoặc b.
`a*`: Không hoặc nhiều lần xuất hiện của a.
`a+`: Một hoặc nhiều lần xuất hiện của a.
`a?`: Không hoặc một lần xuất hiện của a.
`a{3}`: Chính xác 3 lần xuất hiện của a.
`a{3,}`: Ít nhất 3 lần xuất hiện của a.
`a{3,6}`: Từ 3 đến 6 lần xuất hiện của a.

```ruby
# Ví dụ về các mẫu biểu thức
puts "abc".match?(/a.c/)  # Output: true
puts "123".match?(/\d+/)  # Output: true
puts "hello_world".match?(/\w+/)  # Output: true
puts " \t\n".match?(/\s+/)  # Output: true
puts "start".match?(/^start/)  # Output: true
puts "end".match?(/end$/)  # Output: true
puts "a".match?(/[abc]/)  # Output: true
puts "d".match?(/[^abc]/)  # Output: true
puts "a".match?(/a|b/)  # Output: true
puts "aaa".match?(/a*/)  # Output: true
puts "aaa".match?(/a+/)  # Output: true
puts "a".match?(/a?/)  # Output: true
puts "aaa".match?(/a{3}/)  # Output: true
puts "aaaa".match?(/a{3,}/)  # Output: true
puts "aaaa".match?(/a{3,6}/)  # Output: true
```

**Tìm kiếm và thay thế**
Ruby cung cấp các phương thức sub và gsub để thực hiện tìm kiếm và thay thế trong chuỗi sử dụng Regex. Phương thức sub thay thế lần xuất hiện đầu tiên của mẫu, trong khi gsub thay thế tất cả các lần xuất hiện.

```ruby
text = "The quick brown fox jumps over the lazy dog."

# Thay thế lần xuất hiện đầu tiên của "fox" bằng "cat"
new_text = text.sub(/fox/, "cat")
puts new_text  # Output: The quick brown cat jumps over the lazy dog.

# Thay thế tất cả các lần xuất hiện của "the" bằng "a"
new_text = text.gsub(/the/i, "a")
puts new_text  # Output: a quick brown fox jumps over a lazy dog.
```

**Nhóm và tham chiếu lại
Nhóm (grouping) cho phép ta nhóm các phần của biểu thức Regex lại với nhau bằng cách sử dụng dấu ngoặc đơn `()`. Ta có thể tham chiếu lại các nhóm này trong biểu thức hoặc trong chuỗi thay thế bằng cách sử dụng ký tự `$` hoặc `\`.

**Các phương thức Regex trong ruby**
Ruby cung cấp nhiều phương thức để làm việc với Regex, bao gồm:
`=~`: Trả về chỉ số của lần xuất hiện đầu tiên của mẫu trong chuỗi hoặc nil nếu không tìm thấy.
`match`: Trả về một đối tượng MatchData chứa thông tin về lần xuất hiện đầu tiên của mẫu trong chuỗi hoặc nil nếu không tìm thấy.
`scan`: Trả về một mảng chứa tất cả các lần xuất hiện của mẫu trong chuỗi.
`split`: Chia chuỗi thành một mảng các chuỗi con dựa trên mẫu.

```ruby
text = "The quick brown fox jumps over the lazy dog."

# Sử dụng =~
index = text =~ /fox/
puts index  # Output: 16

# Sử dụng match
match_data = text.match(/quick (brown) (fox)/)
puts match_data[0]  # Output: quick brown fox
puts match_data[1]  # Output: brown
puts match_data[2]  # Output: fox

# Sử dụng scan
words = text.scan(/\w+/)
puts words.inspect  # Output: ["The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"]

# Sử dụng split
parts = text.split(/\s+/)
puts parts.inspect  # Output: ["The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"]
```