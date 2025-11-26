## Loop
Ruby có nhiều cách để lặp lại hành động từ `for`, `while` cho đến `each`, `times`, `loop do`
**While**
```ruby
i = 1

while i <= 5
  puts "Lần thứ #{i}"
  i += 1
end

# Hoặc
puts i += 1 while i < 5
```

**Until**
Ngược lại so với `while`, `until` sẽ lặp lại cho đến khi điều kiện đúng

```ruby
i = 1

until i > 5
  puts "Giá trị: #{i}"
  i += 1
end
```

**Loop do**
`loop` là vòng lặp vô hạn, cần phải chủ động dùng `break` để thoát 

```ruby
i = 0

loop do
  i += 1
  puts "Đếm: #{i}"
  break if i == 5
end
```

**For**
```ruby
for i in 1..5
  puts "Số: #{i}"
end
```

**Times**
Lặp `n` lần với n là số lần muốn lặp

```ruby
5.times do |i|
  puts "Lần thứ #{i + 1}"
end
```

**Each**
```ruby
fruits = ["Táo", "Cam", "Chuối"]

fruits.each do |fruit|
  puts "Tôi thích #{fruit}"
end

# Lặp theo phạm vi
(1..3).each do |n|
  puts n
end

# Lặp theo hash
person = {name: "An", age: 25}

person.each do |key, value|
  puts "#{key}: #{value}"
end
```

**Các từ khóa điều khiển vòng lặp**
| Từ khóa | Ý nghĩa                                              |
| ------- | ---------------------------------------------------- |
| `break` | Thoát khỏi vòng lặp ngay lập tức                     |
| `next`  | Bỏ qua phần còn lại trong lần lặp này, sang vòng sau |
| `redo`  | Lặp lại **lần hiện tại** (hiếm dùng)                 |

```ruby
# Next
(1..5).each do |i|
  next if i == 3
  puts i
end

# Break
for i in 0..5
   if i > 2 then
      break
   end
   puts "Value of local variable is #{i}"
end

# Redo
for i in 0..5
   if i < 2 then
      puts "Value of local variable is #{i}"
      redo
   end
end
```

**Retry**
Retry được dùng trong `begin...rescue` để thử lại sau khi bắt lỗi.
```ruby
attempts = 0

begin
  attempts += 1
  puts "Thử lần #{attempts}"
  raise "Lỗi tạm thời" if attempts < 3
rescue
  retry if attempts < 3
end

puts "Thành công sau #{attempts} lần!"
```

Ngoài ra `retry` còn có thể sử dụng trong vòng lặp để bắt đầu lại vòng lặp
```ruby
for i in 1..5
  puts i
  retry if i == 3
end
```

**Ngoài các cách loop như trên ta còn có cách duyệt qua các phần tử (Iterator)**
1. each
2. map
3. select
4. reject
5. times
6. upto/downto
7. each_with_index
8. each_key/each_value

```ruby
# each
[1, 2, 3].each do |n|
  puts n * 2
end
  # Output: 2, 4, 6

# map
result = [1, 2, 3].map { |n| n * 2 }
  # => [2, 4, 6]

# select
[1, 2, 3, 4].select { |n| n.even? }
  # => [2, 4]

#reject
[1, 2, 3, 4].reject { |n| n.even? }
  # => [1, 3]

# times
3.times do |i|
  puts "Lần #{i}"
end
  # Output:
  # Lần 0
  # Lần 1
  # Lần 2

# upto/downto
1.upto(3) { |i| puts i }   # 1, 2, 3
3.downto(1) { |i| puts i } # 3, 2, 1

# each_with_index
["a", "b", "c"].each_with_index do |val, idx|
  puts "#{idx}: #{val}"
end
  # 0: a
  # 1: b
  # 2: c

# each_key/each_value
h = {a: 1, b: 2, c: 3}

h.each_key { |k| puts k }     # a, b, c
h.each_value { |v| puts v }   # 1, 2, 3

```