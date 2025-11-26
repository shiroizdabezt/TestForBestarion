## Array
- Là một tập hợp có thứ tự của các phần tử, có thể chứa bất kỳ kiểu dữ liệu nào
- Là đối tượng của lớp `Array`

**Cách tạo mảng**
| Cách                | Cú pháp                      | Ví dụ               | Kết quả                 |
| ------------------- | ---------------------------- | ------------------- | ----------------------- |
| Dùng ngoặc vuông    | `[a, b, c]`                  | `[1, 2, 3]`         | `[1, 2, 3]`             |
| Dùng `Array.new`    | `Array.new(size, value=nil)` | `Array.new(3, "a")` | `["a", "a", "a"]`       |
| Từ chuỗi `.split`   | `"a,b,c".split(",")`         |                     | `["a", "b", "c"]`       |
| Dùng `%w` hoặc `%W` | `%w[a b c]`, `%W[a #{b}]`    | `%w[ruby is fun]`   | `["ruby", "is", "fun"]` |

**Truy cập, thêm xóa sửa phần tử**
```ruby
arr = [10, 20, 30, 40, 50]

arr[0]      # => 10 (phần tử đầu)
arr[-1]     # => 50 (phần tử cuối)
arr[1, 3]   # => [20, 30, 40]
arr[2..4]   # => [30, 40, 50]

arr[1] = 99 # thay đổi giá trị
# => [10, 99, 30, 40, 50]
```

| Phương thức            | Mô tả                           | Ví dụ              | Kết quả          |
| ---------------------- | ------------------------------- | ------------------ | ---------------- |
| `push` hoặc `<<`       | Thêm cuối                       | `[1,2] << 3`       | `[1,2,3]`        |
| `unshift`              | Thêm đầu                        | `[1,2].unshift(0)` | `[0,1,2]`        |
| `pop`                  | Xóa cuối, trả về phần tử bị xóa | `[1,2,3].pop`      | `[1,2]`          |
| `shift`                | Xóa đầu, trả về phần tử bị xóa  | `[1,2,3].shift`    | `[2,3]`          |
| `insert(index, value)` | Chèn vào vị trí                 | `a.insert(1, "x")` | `[1, "x", 2, 3]` |

**Cách duyệt mảng và một số phương thức thông dụng**
```ruby
arr = [10, 20, 30]

arr.each { |x| puts x }         # Duyệt từng phần tử
arr.map { |x| x * 2 }           # => [20, 40, 60]
arr.select { |x| x > 15 }       # => [20, 30]
arr.reject { |x| x < 25 }       # => [30]
arr.find { |x| x == 20 }        # => 20
arr.reduce(:+)                  # => 60
```

| Phương thức       | Tác dụng                 | Ví dụ                 | Kết quả      |
| ----------------- | ------------------------ | --------------------- | ------------ |
| `length` / `size` | Số phần tử               | `[1,2,3].size`        | `3`          |
| `empty?`          | Kiểm tra rỗng            | `[1,2].empty?`        | `false`      |
| `include?`        | Kiểm tra có chứa giá trị | `[1,2,3].include?(2)` | `true`       |
| `first`, `last`   | Lấy phần tử đầu/cuối     | `[1,2,3].first`       | `1`          |
| `reverse`         | Đảo ngược                | `[1,2,3].reverse`     | `[3,2,1]`    |
| `uniq`            | Loại trùng               | `[1,1,2,2].uniq`      | `[1,2]`      |
| `sort`            | Sắp xếp                  | `[3,1,2].sort`        | `[1,2,3]`    |
| `flatten`         | Làm phẳng mảng lồng nhau | `[1, [2,3]].flatten`  | `[1,2,3]`    |
| `compact`         | Xóa `nil`                | `[1, nil, 2].compact` | `[1,2]`      |
| `join`            | Ghép thành chuỗi         | `[1,2,3].join('-')`   | `"1-2-3"`    |
| `sample`          | Lấy ngẫu nhiên 1 phần tử | `[10,20,30].sample`   | `20` (ví dụ) |
