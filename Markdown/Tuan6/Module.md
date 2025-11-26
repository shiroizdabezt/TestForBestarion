## Module

Module trong Ruby là một tập hợp các method, hằng số, và class khác, được dùng để:
- Tổ chức và chia sẻ mã,
- Tránh trùng tên class/method,
- Thêm chức năng vào class (qua mixin),
- Tạo namespace.

```ruby
module Greetings
  def say_hello
    puts "Xin chào!"
  end
end

class Person
  include Greetings
  # Hoặc
  extend Greeting
end

# Include
p = Person.new
p.say_hello   # => "Xin chào!"

# Extend
Person.say_hello
```
**Require**

Ngoài ra ta có thể module từ một file khác vào file hiện tại bằng cách sử dụng `require` 
```ruby
# Module trong file math.rb
module MathTools
  def self.square(x)
    x * x
  end
end

# File main
require './math'   # nạp file (không cần .rb)
puts MathTools.square(4) # => 16

```

**Mixin**
Mixin là cách Ruby cho phép thêm chức năng từ module vào class mà không cần kế thừa
```ruby
module Walkable
  def walk
    puts "Tôi đang đi bộ!"
  end
end

module Talkable
  def talk
    puts "Tôi đang nói chuyện!"
  end
end

class Person
  include Walkable
  include Talkable
end

p = Person.new
p.walk   # => "Tôi đang đi bộ!"
p.talk   # => "Tôi đang nói chuyện!"
```