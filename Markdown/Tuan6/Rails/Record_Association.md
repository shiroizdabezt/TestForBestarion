## Record Association
- Association là cách Rails định nghĩa mối quan hệ giữa các bảng trong db
  - Một user có nhiều orders
  - Một order thuộc về một customer
  - Một product có nhiều order_items
  - Một order có nhiều product thông qua order_items
- Một số loại association chính trong Rails:
  - `belongs_to`: Dùng ở phía con, bảng có **foreign key**
``` ruby
class Order < ApplicationRecord
  belongs_to :customer
end

# Order phải có cột customer_id
```
  - `has_many`: Dùng ở phía cha, bảng được tham chiếu bởi bảng khác
```ruby
class Customer < ApplicationRecord
  has_many :orders
end
```
  - `has_one`: Một một
```ruby
class User < ApplicationRecord
  has_one :profile
end
```
  - `has_many :through`: Dùng khi có bảng trung gian
    - Ví dụ: Order -> OrderItem -> Product
```ruby
class Order < ApplicationRecord
  has_many :order_items
  has_many :products, through: :order_items
end

class OrderItem < ApplicationRecord
  belongs_to :order
  belongs_to :product
end

class Product < ApplicationRecord
  has_many :order_items
  has_many :orders, through: :order_items
end
```
  - `has_and_belongs_to_many`: Quan hệ nhiều-nhiều không có model trung gian. 
```ruby
class User < ApplicationRecord
  has_and_belongs_to_many :roles
end
```
