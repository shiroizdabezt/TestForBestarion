## Block

Block trong Ruby là một đoạn mã có thể truyền vào method để thực thi.
Hoạt động giống như một hàm ẩn danh (anonymous function) — không có tên, nhưng có thể được gọi bằng yield hoặc chuyển vào method bằng ký hiệu &block được bao quanh bởi {} hoặc do ... end.

```ruby
def greeting
  puts "Bắt đầu"
  yield
  puts "Kết thúc"
end

greeting { puts "Xin chào từ block!" }

# Bắt đầu
# Xin chào từ block!
# Kết thúc

def say_hello
  yield("An")   # truyền "An" vào block
end

say_hello { |name| puts "Xin chào, #{name}!" }
# => "Xin chào, An!"

def repeat(n, &block)
  n.times(&block)  # hoặc: n.times { block.call }
end

repeat(3) { puts "Hello!" }

```
