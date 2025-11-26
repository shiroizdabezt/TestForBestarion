## If..else
- Câu điều kiện trong ruby cũng có cú pháp gần tương tự như những ngôn ngữ lập trình khác

```ruby
x = 1
if x > 2
   puts "x is greater than 2"
elsif x <= 2 and x!=0
   puts "x is 1"
else
   puts "I can't guess the number"
end
```
Ta cũng có thể sử dụng câu điều kiện khi chỉ có một lệnh đơn giản
```ruby
puts "Trời lạnh!" if temperature < 20
```

Ngoài ra còn có `unless` ngược lại với `if`
```ruby
x = 1 
unless x>=2
   puts "x is less than 2"
 else
   puts "x is greater than 2"
end
```

### Case
- Ta có case nếu khớp với trường hợp nào sẽ thực hiện các lệnh tương ứng

```ruby
day = "Monday"

case day
when "Monday"
  puts "Đầu tuần rồi, cố lên!"
when "Friday"
  puts "Sắp cuối tuần!"
else
  puts "Một ngày bình thường thôi."
end
```

### Unless
- Unless ngược so với if nghĩa là nếu không
```ruby

```