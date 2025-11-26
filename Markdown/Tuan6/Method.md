## Method
- Method là khối lệnh có thể tái sử dụng được định nghãi bằng `def` và kết thúc bằng `end`

```ruby
def hello
  puts "Xin chào Ruby!"
end

hello  # => "Xin chào Ruby!"
```
**Class Method**
Khi một phương thức được định nghĩa bên ngoài phần định nghĩa của lớp, nó mặc định sẽ được đánh dấu là private.
Ngược lại, các phương thức được định nghĩa bên trong phần thân của lớp sẽ mặc định là public.

```ruby
class Person
  def greet
    puts "Xin chào!"
  end
end

p = Person.new
p.greet   # ✅


class Accounts
  def self.return_date
    puts "Ngày trả sách là ngày 15"
  end
end

Accounts.return_date  # ✅
```

**Alias**
Alias trong Ruby dùng để tạo bí danh (tên thay thế) cho phương thức (method) hoặc biến toàn cục (global variable).
Không thể định nghĩa alias bên trong thân của một method.
Alias của method sẽ giữ nguyên định nghĩa hiện tại của method đó, ngay cả khi sau này method gốc bị ghi đè.
```ruby
def greet
  puts "Xin chào!"
end

alias hello greet   # tạo bí danh "hello" cho "greet"

greet   # => "Xin chào!"
hello   # => "Xin chào!"
```

**Undef**
Vô hiệu hóa hoặc ngăn kế thừa một method từ superclass.
```ruby
def bar
  puts "Hello"
end

bar  # => "Hello"

undef bar

bar  # ❌ Lỗi: undefined method `bar'
```