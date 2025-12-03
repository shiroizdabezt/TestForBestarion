# 🗓️ Tuần 8 — Bắt đầu / Week 8 — Getting Started

> 📅 **Thời gian / Duration:** 24/11/2025 – 28/11/2025  
> 🧭 **Intern:** <UserName>  Nguyễn Đình Khoa  
> 🧑‍🏫 **Mentor:** ___________________

---

## 🎯 Mục tiêu Tuần / Weekly Goals


---

## 📆 Tiến độ theo ngày / Daily Progress

---

### 📅 Thứ 2 — 24/11/2025 

#### ✅ Nhiệm vụ / Tasks
- Đọc và phân tích các file build daily
- Tìm hiểu về Solr

#### 📘 Ghi chú / Notes


#### ⚙️ Khó khăn / Issues


#### 📚 Học được / Learnings
**Phân tích logic script build daily (Mã giả):**

1. **Lọc lỗi (Log Filtering):**
   - Đọc file log.
   - Loại bỏ các dòng bắt đầu bằng chuỗi ngoại lệ (ví dụ: 'abc').
   - Lọc lấy các dòng chứa từ khóa "error" (không phân biệt hoa thường).

2. **Xác định danh sách Build (Database Selection):**
   - **Nếu** yêu cầu build TẤT CẢ (ALL):
     - Truy vấn danh sách ID các tổ chức ưu tiên từ cấu hình.
     - Thiết lập thứ tự ưu tiên: Các tổ chức trong danh sách ưu tiên sẽ được xử lý trước.
   - **Ngược lại** (Build danh sách cụ thể):
     - Phân tích danh sách ID đầu vào.
     - Truy vấn thông tin chi tiết (ID, Tên DB, IP) của các tổ chức đó từ Database chính.

3. **Quản lý tiến trình song song (Queue Management):**
   - Khởi tạo hàng đợi để theo dõi các tiến trình đang chạy (PID).
   - **Hàm Cập nhật hàng đợi:** Kiểm tra trạng thái các tiến trình, loại bỏ tiến trình đã hoàn thành khỏi danh sách theo dõi.
   - **Hàm Kiểm tra hàng đợi:** Nếu số lượng tiến trình đang chạy đạt giới hạn tối đa, đợi cho đến khi có tiến trình kết thúc.

4. **Quy trình Build chi tiết (Build Function):**
   - **Đầu vào:** ID tổ chức, Tên Database.
   - Ghi log bắt đầu build cho tổ chức.
   - **Lặp** qua từng bước build (ví dụ: step14, step2, step3, step4):
     - Ghi log bắt đầu bước hiện tại.
     - Gọi script thực thi build dữ liệu cho bước đó.
   - Ghi log hoàn thành build cho tổ chức.

5. **Luồng xử lý chính (Main Loop):**
   - Tạo thư mục lưu log nếu chưa tồn tại.
   - **Lặp** qua từng tổ chức trong danh sách cần build:
     - Trích xuất thông tin: ID, Database, Host.
     - Gọi hàm **Quy trình Build chi tiết** chạy ngầm (background).
     - Thêm tiến trình vừa tạo vào hàng đợi quản lý.
     - **Trong khi** số lượng tiến trình đang chạy >= Số luồng tối đa cho phép:
       - Chạy hàm **Kiểm tra hàng đợi**.
       - Tạm dừng một khoảng thời gian ngắn trước khi kiểm tra lại.


---

### 📅 Thứ 3 — 25/11/2025

#### ✅ Nhiệm vụ / Tasks
- Thực hiện chạy Solr
- Thực hiện chạy Zookeeper
- Kết nối bộ code mssss vào Solr Vim và Solr Contract

#### 📘 Ghi chú / Notes


#### ⚙️ Khó khăn / Issues


#### 📚 Học được / Learnings
- [Solr](Solr/Solr.md#solr) 
- [Quy trình hoạt động](Solr/Solr.md#quy-trình-hoạt-động)
- [So sánh Solr với DB truyền thống](Solr/Solr.md#so-sánh-solr-với-tìm-kiếm-database-truyền-thống)
- [Các loại Solr](Solr/Solr.md#các-loại-solr)
- [Zookeeper](Solr/Solr.md#zookeeper)
- [Cách cài đặt Solr](Solr/Solr.md#cách-cài-đặt-solr)
---

### 📅 Thứ 4 — 26/11/2025

#### ✅ Nhiệm vụ / Tasks
- Đọc bộ code phần build solr hide và show

#### 📘 Ghi chú / Notes

#### ⚙️ Khó khăn / Issues
- Vẫn chưa thể chạy được phần build đó


#### 📚 Học được / Learnings


---

### 📅 Thứ 5 — 27/11/2025
OFF


---

### 📅 Thứ 6 — 28/11/2025

#### ✅ Nhiệm vụ / Tasks
- Tiếp tục đọc và chạy bộ code phần build solr hide và show

#### 📘 Ghi chú / Notes


#### ⚙️ Khó khăn / Issues
- Chưa thể hiện VIM lên giao diện web

#### 📚 Học được / Learnings
- Đã chạy được phần build solr hide và show. [Cách sửa](./images/solr.bash)

lan 1: commit docs