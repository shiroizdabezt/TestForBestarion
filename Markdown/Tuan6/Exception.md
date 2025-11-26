## Exceptions
- Exception là lỗi phát sinh trong quá trình chạy chương trình, khiến chương trình bị gián đoạn
- Ruby xử lý các lỗi này bằng cơ chế exception handling, có thể bắt lỗi và xử lý thay vì để chương trình dừng đột ngột
  
### Cú pháp

**Begin-rescue**
Cú pháp cơ bản để xử lý exceptions trong ruby là sử dụng khối `begin-rescue-end`. Khối `begin` chứa mã có thể gây ra exceptions và khối `rescue` chứa mã để xử lý exceptions đó
```ruby
begin
  # Mã có thể gây ra exceptions
  result = 10 / 0
rescue SomeExceptionClass => e
  # Mã để xử lý exceptions
  puts "Lỗi: #{e.message}"
end
```

**Retry**
`retry` cho phép chương trình thử lại một thao tác đã thất bại. Câu lệnh này thường được dùng trong khối `rescue` để thử lại thao tác gây ra exceptions

```ruby
attempts = 0

begin
  attempts += 1
  puts "Thử lần thứ #{attempts}"
  raise "Lỗi giả lập" if attempts < 3
rescue => e
  puts e.message
  retry if attempts < 3
end
```

**Raise**
`raise` cho phép ta tự tạo ra exceptions. Ta có thể sử dụng câu lệnh này để báo hiệu một điều kiện lỗi hoặc để tái tọa lại một exception đã bắt được

```ruby
def divide(x, y)
  raise ArgumentError, "Không thể chia cho 0" if y == 0
  x / y
end

begin
  result = divide(10, 0)
rescue ArgumentError => e
  puts "Lỗi: #{e.message}"
end
```

**Ensure**
`ensure` cho phép ta chỉ định mã sẽ luôn thực thi, bất kể có exceptions xảy ra hay không. 
```ruby
file = File.open("example.txt", "w")

begin
  file.puts "Hello, World!"
rescue IOError => e
  puts "Lỗi: #{e.message}"
ensure
  file.close unless file.nil?
end
```

**Else**
`else` cũng có thể giúp ta chỉ định mã sẽ được thực thi nếu không có exceptions nào xả ra trong khối begin

```ruby
begin
  result = 10 / 2
rescue ZeroDivisionError => e
  puts "Lỗi: #{e.message}"
else
  puts "Kết quả: #{result}"
end
```

**Catch và Throw**
`catch` và `throw` cung cấp một cách để thoát ra khỏi các vòng lặp lồng nhau hoặc các lời gọi phương thức. Câu lệnh `catch` định nghĩa một khối mã có thể thoát ra bằng cách thực thi câu lệnh `throw`

```ruby
catch(:done) do
  (1..10).each do |i|
    if i == 5
      throw :done, "Đã tìm thấy số 5"
    end
  end
end
```

**Class Exceptions**
`exception` là lớp cơ sở cho tất cả các exceptions trong ruby. Ta có thể tạo ra các lớp exceptions tùy chỉnh bằng cách kế thừa từ lớp `StandardError` hoặc một trong các lớp con của nó

```ruby
class MyCustomError < StandardError
  attr_reader :details

  def initialize(message, details)
    super(message)
    @details = details
  end
end

begin
  raise MyCustomError.new("Đã xảy ra lỗi tùy chỉnh", "Chi tiết lỗi")
rescue MyCustomError => e
  puts "Lỗi: #{e.message}"
  puts "Chi tiết: #{e.details}"
end
```

Class Exception còn có nhiều phương thức hữu ích 
  - `message`: Trả về thông báo lỗi
  - `backtrace`: Trả về mảng các chuỗi mô tả ngăn xếp gọi khi exceptions xảy ra lỗi
  - `cause`: trả về các exceptions gốc nếu exceptions hiện tại được tạo ra bởi một exceptions khác 

```ruby
begin
  raise "Lỗi ban đầu"
rescue => e
  raise "Lỗi thứ hai" rescue nil
end

begin
  raise "Lỗi thứ ba"
rescue => e
  puts "Thông báo lỗi: #{e.message}"
  puts "Ngăn xếp gọi: #{e.backtrace.join("\n")}"
  puts "Nguyên nhân: #{e.cause.message}" if e.cause
end
```