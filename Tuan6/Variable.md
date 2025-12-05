## Variables trong Ruby Class
Trong Class của Ruby có 4 loại biến ta có thể sử dụng: Local Variables (biến cục bộ), Instance Variables (biến khởi tạo), Class Variable (biến lớp), Global Variable (biến toàn cục), Constant (hằng số), Psuedo-Variable (biến giả)

**Local Variable**
- Là các biến được định nghĩa bên trong một phương thức, khối hoặc một vòng lặp
- Chỉ có hiệu lực trong phạm vi mà nó được khai báo
- Bắt đầu bằng chữ thược hoặc dấu gạch dưới (`_`).

```ruby
def say_hello
  message = "Hello, Ruby!"   # local variable
  puts message
end

say_hello
# puts message  # => Lỗi, vì biến message không tồn tại ngoài method
```

**Instance Variables**
- Dành cho mỗi đối tượng của một lớp
- Mỗi đối tượng có bản sao riêng của biến này
- Bắt đầu bằng @

Có nhiều cách viết một hàm có sử dụng biến Instance.  
Cách viết thủ công.  
```ruby
class Person
  def initialize(name)
    @name = name      # instance variable
  end

  def greet
    puts "Hello, my name is #{@name}"
  end
end

person1 = Person.new("Alice")
person2 = Person.new("Bob")

person1.greet   # => Hello, my name is Alice
person2.greet   # => Hello, my name is Bob
```
  
Cách viết dùng attr_accessor
```ruby
class Person
  attr_accessor :name, :age, :city

  def initialize(name, age, city)
    @name = name
    @age = age
    @city = city
  end
end

person = Person.new("Bob", 22, "HCMC")
puts person.name   # => Bob
person.age = 23
puts person.age    # => 23
```

Sử dụng hash để khởi tạo linh hoạt
```ruby
class Person
  def initialize(info)
    @name = info[:name]
    @age = info[:age]
    @city = info[:city]
  end

  def show_info
    puts "#{@name}, #{@age} years old, from #{@city}."
  end
end

person = Person.new({ name: "Alice", age: 25, city: "Hue" })
person.show_info  # => Alice, 25 years old, from Hue.
```

**Class Variable**
- Dùng chung giữa tất cả các đối tượng của cùng một lớp
- Thuộc về lớp chứ không thuộc về từng instance
- Bắt đầu bằng @@

```ruby
class Person
  @@count = 0

  def initialize(name)
    @name = name
    @@count += 1
  end

  # instance method
  def show_count
    puts "Total (from instance): #{@@count}"
  end

  # class method
  def self.total_people #Thêm self. để có thể gọi thẳng từ class mà không cần qua biến
    puts "Total (from class): #{@@count}"
  end
end

p1 = Person.new("Alice")
p2 = Person.new("Bob")

p1.show_count        # => Total (from instance): 2
Person.total_people  # => Total (from class): 2
```

**Global Variable**
- Có thể truy cập ở bất kỳ đâu trong chương trình
- Bắt đầu bằng $

```ruby
$version = "1.0"   # global variable

class App
  def show_version
    puts "App version: #{$version}"
  end
end

app = App.new
app.show_version      # => App version: 1.0
puts $version         # => 1.0 (dùng được ngoài class)
```

**Constant**
- Là biến giá trị cố định, được dùng để lưu trữ các giá trị không nên thay đổi trong suốt chương trình
- Tên phải bắt đầu bằng chữ in hoa
- Theo quy ước, hằng số viết in hoa toàn bộ và nếu có nhiều từ thì nối bằng dấu (`_`)

```ruby
class Example
   VAR1 = 100
   VAR2 = 200
   def show
      puts "Value of first Constant is #{VAR1}"
      puts "Value of second Constant is #{VAR2}"
   end
end

# Create Objects
object = Example.new()
object.show
```
**Pseudo-Variables**
- Là các biến đặc biệt do Ruby cung cấp sẵn, chúng không thể gán giá trị trực tiếp, nhưng vẫn sẵn có giá trị hệ thống hoặc ngữ cảnh đặc biệt
  
Danh sách các Pseudo-Variables chính trong ruby  
|Pseudo-Variables| Ý nghĩa|
|:--------------:|--------|
|`self`|Đại diện cho đối tượng hiện tại (current object)|
|`nil`|Đại diện cho giá trị trống (nothing)|
|`true`|Đại diện cho giá trị đúng|
|`false`|Đại diện cho giá trị sai|
|`__FILE__`|Tên của file Ruby hiện tại đang chạy|
|`__LINE__`|Số dòng hiện tại trong file|
|`__ENCODING__`|Kiểu mã hóa của file nguồn|



### Arrays
- Array là một tập hợp có thứ tự của các giá trị (phần tử)
- Mỗi phần tử có thể khác kiểu (String, Number, Object, Hash, v.v)
- Mảng được bao bởi `[]` và các phần tử cách nhau bằng dấu `,`

```ruby
arr = [1, 2, 3, "Ruby", true]
puts arr.inspect
# => [1, 2, 3, "Ruby", true]

ary = [  "fred", 10, 3.14, "This is a string", "last element", ]
ary.each do |i|
   puts i
end

# fred
# 10
# 3.14
# This is a string
# last element
```
Thêm xóa sửa phần từ trong mảng
| Hành động| Cú pháp| Mô tả|
| ---------------- | ----------------------------- | ------------------------------- |
| Thêm cuối | `arr << x` hoặc `arr.push(x)` | Thêm phần tử vào cuối|
| Thêm đầu  | `arr.unshift(x)` | Thêm vào đầu |
| Xóa cuối  | `arr.pop`| Xóa đi phần tử cuối|
| Xóa đầu   | `arr.shift`| Xóa đi phần tử đầu |
| Xóa theo chỉ số  | `arr.delete_at(i)`| Xóa phần tử vị trí `i`|
| Xóa theo giá trị | `arr.delete("Ruby")` | Xóa phần tử có giá trị `"Ruby"` |

Các method hữu ích của Array
| Method                 | Tác dụng                                | Ví dụ                               |
| ---------------------- | --------------------------------------- | ----------------------------------- | 
| `.length` hoặc `.size` | Đếm số phần tử                          | `[1,2,3].length → 3`                |  
| `.include?(x)`         | Kiểm tra có chứa phần tử không          | `[1,2,3].include?(2) → true`        |  
| `.first`, `.last`      | Lấy phần tử đầu/cuối                    | `[1,2,3].last → 3`                  |  
| `.reverse`             | Đảo ngược                               | `[1,2,3].reverse → [3,2,1]`         |  
| `.sort`                | Sắp xếp                                 | `[3,1,2].sort → [1,2,3]`            |  
| `.join(",")`           | Gộp thành chuỗi                         | `["a","b","c"].join(",") → "a,b,c"` | 
| `.map`                 | Tạo mảng mới sau khi xử lý từng phần tử | `[1,2,3].map {x  x*2 } → [2,4,6]`   |
| `.select`              | Lọc phần tử theo điều kiện              | `[1,2,3,4].select {x  x.even? } → [2,4]`|

### Hash
- Hash là tập hợp các cặp key-value
- Giống như "object" trong JavaScript hoặc "dict" trong Python

```ruby
person = {
  "name" => "Alice",
  "age" => 25,
  "city" => "Hanoi"
}

puts person["name"]   # => Alice
puts person["age"]    # => 25

# Hoặc

person = {
  name: "Alice",
  age: 25,
  city: "Hanoi"
}

puts person[:city]   # => Hanoi

```
Truy cập thêm xóa sửa các phần tử
| Hành động      | Cú pháp              | Ví dụ                  |
| -------------- | -------------------- | ---------------------- |
| Truy cập       | `hash[:key]`         | `person[:age]`         |
| Thêm mới / sửa | `hash[:key] = value` | `person[:job] = "Dev"` |
| Xóa key        | `hash.delete(:key)`  | `person.delete(:age)`  |
| Đếm phần tử    | `hash.length`        | `person.length`        |
| Kiểm tra key   | `hash.key?(:city)`   | `true`                 |
| Kiểm tra value | `hash.value?(25)`    | `true`                 |

Một số method của Hash
| Method       | Mô tả                  | Ví dụ                                |
| ------------ | ---------------------- | ------------------------------------ |
| `.keys`      | Trả về danh sách key   | `{a:1,b:2}.keys → [:a, :b]`          |
| `.values`    | Trả về danh sách value | `{a:1,b:2}.values → [1,2]`           |
| `.merge(h2)` | Gộp 2 hash             | `{a:1}.merge({b:2}) → {:a=>1,:b=>2}` |
| `.invert`    | Đổi vị trí key ↔ value | `{a:1,b:2}.invert → {1=>:a,2=>:b}`   |
| `.empty?`    | Kiểm tra hash rỗng     | `{}`.empty? → true                   |

### Range
- Range trong Ruby đại diện cho một các giá trị liên tiếp có điểm đầu và điểm kết thúc

```ruby
(start..end)     # bao gồm cả giá trị cuối
(start...end)    # loại trừ giá trị cuối
Range.new(start, end, exclude_end = false)
```
Các method thường dùng với Range
| Method             | Ý nghĩa                        | Ví dụ                                |
| ------------------ | ------------------------------ | ------------------------------------ |
| `.to_a`            | Chuyển range thành mảng        | `(1..4).to_a → [1,2,3,4]`            |
| `.include?(x)`     | Kiểm tra có chứa giá trị không | `(1..5).include?(3) → true`          |
| `.first` / `.last` | Lấy phần tử đầu/cuối           | `(10..20).first(3) → [10,11,12]`     |
| `.min` / `.max`    | Giá trị nhỏ nhất / lớn nhất    | `(1..9).max → 9`                     |
| `.size`            | Đếm số phần tử (nếu range số)  | `(1..5).size → 5`                    |
| `.step(n)`         | Duyệt theo bước nhảy           | `(1..10).step(2).to_a → [1,3,5,7,9]` |

### Dot and Double Colon
- Toán tử  `.` (Dot) được dùng để gọi phương thức (method) hoặc truy cập thuộc tính của một đối tượng (object).
- Toán tử `::` (Double Colon) được dùng để truy cập hằng số (constant), class, module, hoặc phương thức cấp lớp (class method) ở bên trong một class/module khác

