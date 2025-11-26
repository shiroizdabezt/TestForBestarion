## OOP
- Lập trình hướng đối tượng (OOP) là một phương pháp lập trình mạnh mẽ và phổ biến, giúp tổ chức mã nguồn một cách rõ ràng và dễ bảo trì. 
- Ruby là một ngôn ngữ lập trình hướng đối tượng hoàn toàn, trong đó mọi thứ đều là đối tượng.

**Định nghĩa Class**
Trong ruby có thể định nghĩa một lớp bằng từ khóa `class` và kết thúc bằng từ khóa `end`. Tên class phải bắt đầu bằng chữ cái in hoa

```ruby
class Car
  # Nội dung của lớp
end
```

**Phương thức khởi tạo**
`initialize` là một phương thức khởi tạo đặc biệt trong ruby được gọi tự động khi một đối tượng  của lớp được tạo ra bằng phương thức `new`.
Phương thức này thường được sử dụng để khởi tạo các biến instance

```ruby
class Car
  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end
end

car = Car.new("Toyota", "Corolla", 2020)
```

**Biến Instance**
Biến `instance` trong ruby bắt đầu bằng ký tự `@` và chỉ có thể được truy cập từ bên trong của đối tượng. Mỗi đối tượng có các biến instance của riêng nó

```ruby
class Car
  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end

  def display_details
    puts "Make: #{@make}, Model: #{@model}, Year: #{@year}"
  end
end

car = Car.new("Toyota", "Corolla", 2020)
car.display_details
```

**Phương thức truy cập và thiết lập**
Ruby cung cấp các phương thức `attr_accessor`, `attr_reader`, `attr_writer` để tự động tạo các phương thức truy cập và thiết lập cho các biến instance

```ruby
class Car
  attr_accessor :make, :model, :year

  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end
end

car = Car.new("Toyota", "Corolla", 2020)
puts car.make  # Output: Toyota
car.year = 2021
puts car.year  # Output: 2021
```

**Phương thức instance**
Phương thức instance là các phương thức được định nghĩa trong lớp và có thể được gọi trên các đối tượng của lớp đó

```ruby
class Car
  attr_accessor :make, :model, :year

  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end

  def display_details
    puts "Make: #{@make}, Model: #{@model}, Year: #{@year}"
  end
end

car = Car.new("Toyota", "Corolla", 2020)
car.display_details
```

**Phương thức và biến lớp**
Phương thức lớp là các phương thức được định nghĩa trên lớp thay vì trên các đối tượng của lớp. Chúng được định nghĩa bằng cách sử dụng từ khóa `self`

```ruby
class Car
  @@count = 0

  def self.count
    @@count
  end

  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
    @@count += 1
  end
end

car1 = Car.new("Toyota", "Corolla", 2020)
car2 = Car.new("Honda", "Civic", 2021)
puts Car.count  # Output: 2
```

**Phương thức to_s**
Phương thức `to_s` được sử dụng để trả về một chuỗi đại diện cho đối tượng. nó thường được ghi đè để cung cấp một biểu diễn chuỗi tùy chỉnh của đối tượng

```ruby
class Car
  attr_accessor :make, :model, :year

  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end

  def to_s
    "Make: #{@make}, Model: #{@model}, Year: #{@year}"
  end
end

car = Car.new("Toyota", "Corolla", 2020)
puts car.to_s  # Output: Make: Toyota, Model: Corolla, Year: 2020
```

**Access Control**
Ruby cung cấp ba mức độ bảo vệ cho các phương thức: `public`, `protected` và `private`. Mặc định, các phương thức là `public`, trừ phương thức `initialize` luôn là `private`

```ruby
class Car
  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end

  def display_details
    puts "Make: #{@make}, Model: #{@model}, Year: #{@year}"
  end

  private

  def secret_method
    puts "This is a secret method."
  end
end

car = Car.new("Toyota", "Corolla", 2020)
car.display_details
# car.secret_method  # Lỗi: private method `secret_method' called
```

**Kế thừa Class**
Kế thừa cho phép một lớp con kế thừa các thuộc tính và phương thức của một lớp cha. Ta chỉ cần sử dụng ký tự `<` để chỉ định điều đó

```ruby
class Vehicle
  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end

  def display_details
    puts "Make: #{@make}, Model: #{@model}, Year: #{@year}"
  end
end

class Car < Vehicle
  def initialize(make, model, year, doors)
    super(make, model, year)
    @doors = doors
  end

  def display_details
    super
    puts "Doors: #{@doors}"
  end
end

car = Car.new("Toyota", "Corolla", 2020, 4)
car.display_details
```

**Ghi đè phương thức**
Ghi đè phương thức cho phép một lớp con cung cấp một triển khai khsac cho một phương thức đã được định nghĩa trong lớp cha

```ruby
class Vehicle
  def start
    puts "Vehicle is starting."
  end
end

class Car < Vehicle
  def start
    puts "Car is starting."
  end
end

car = Car.new
car.start  # Output: Car is starting.
```

**Nạp chồng toán tử**
Ruby cho phép ghi đè các toán tử để cung cấp hành vi tùy hcinhr cho các đối tượng

```ruby
class Box
  attr_accessor :width, :height

  def initialize(width, height)
    @width = width
    @height = height
  end

  def +(other)
    Box.new(@width + other.width, @height + other.height)
  end

  def to_s
    "Width: #{@width}, Height: #{@height}"
  end
end

box1 = Box.new(10, 20)
box2 = Box.new(30, 40)
box3 = box1 + box2
puts box3  # Output: Width: 40, Height: 60
```

**Đóng băng đối tượng**
`freeze` Được sử dụng để đóng băng một đối tượng ngăn ko cho nó bị thay đổi

```ruby
str = "Hello"
str.freeze
# str << " World"  # Lỗi: can't modify frozen String
```
**Hằng số Class**
Hằng số class là giá trị không thay đổi được định nghĩa trong lớp. Chúng bắt đầu bằng chữ cái viết hoa

```ruby
class Car
  WHEELS = 4

  def self.wheels
    WHEELS
  end
end

puts Car.wheels  # Output: 4
```

**Tạo đối tượng bằng allocate**
`allocate` tạo một đối tượng mà không gọi phương thức khởi tạo `initialize`

```ruby
class Car
  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end
end

car = Car.allocate
puts car.inspect  # Output: #<Car:0x00007f9c8b0b8b88>
```

**Thông tin class**
Ruby cung cấp nhiều phương thức để lấy thông tin về lớp và đối tượng

