# Giám sát Database PostgreSQL với Prometheus và Grafana
## 1. Cấu hình Postgres Exporter

Trong mô hình giám sát này, `postgres-exporter` đóng vai trò là "người trung gian". Nó kết nối trực tiếp vào Database PostgreSQL, lấy các thông số (metrics) và mở một cổng (mặc định 9187) để Prometheus có thể đến thu thập dữ liệu.

Cấu hình trong `docker-compose.yml`:

```yaml
  postgres-exporter:
    image: prometheuscommunity/postgres-exporter
    container_name: monitoring_postgres_exporter
    ports:
      - "9187:9187"
    environment:
      DATA_SOURCE_NAME: "postgresql://tester:123@172.16.4.15:6432/todo_app_development?sslmode=disable"
      PG_EXPORTER_AUTO_DISCOVER_DATABASES: "false" 
    restart: always
```

### Giải thích các tham số:

*   **`DATA_SOURCE_NAME`**: Chuỗi kết nối đến Database.
    *   `postgresql://`: Giao thức kết nối.
    *   `tester:123`: Username và Password để đăng nhập vào DB. **Lưu ý**: User này cần có quyền đọc các bảng hệ thống (`pg_stat_...`) để lấy metrics.
    *   `172.16.4.15:6432`: Địa chỉ IP và Port của Database (hoặc PgBouncer).
    *   `/todo_app_development`: Tên database cụ thể cần giám sát.
    *   `?sslmode=disable`: Tắt SSL (thường dùng trong mạng nội bộ).
*   **`PG_EXPORTER_AUTO_DISCOVER_DATABASES: "false"`**: Tắt tính năng tự động dò tìm tất cả DB. Khi đã chỉ định rõ DB trong `DATA_SOURCE_NAME`, nên tắt tính năng này để tránh lỗi quyền hạn hoặc kết nối không cần thiết.

## 2. Cơ chế thu thập dữ liệu của Prometheus

Trong file `prometheus.yml`, Prometheus được cấu hình để "scrape" (cào) dữ liệu từ exporter này định kỳ:

```yaml
      - targets: 
          - 'postgres-exporter:9187'
```

Prometheus sẽ định kỳ (ví dụ 10s/lần) gọi vào API `http://postgres-exporter:9187/metrics` để lấy các chỉ số như số lượng kết nối, số transaction, cache hit ratio, v.v.

## 3. Hiển thị dữ liệu trên Grafana

Để trực quan hóa các metrics thu thập được, ta sử dụng Grafana.

### Bước 1: Thêm Data Source
1.  Đăng nhập Grafana (`http://localhost:3000`).
2.  Vào **Connections** -> **Data sources** -> **Add data source**.
3.  Chọn **Prometheus**.
4.  **Prometheus server URL**: Nhập `http://prometheus:9090` (tên service trong docker network).
5.  Bấm **Save & Test**.

### Bước 2: Import Dashboard
Sử dụng Dashboard có sẵn là cách nhanh nhất để có giao diện giám sát chuyên nghiệp.
1.  Vào menu **Dashboards** -> **New** -> **Import**.
2.  Nhập ID dashboard: **9628** (Đây là dashboard phổ biến và chi tiết cho PostgreSQL).
3.  Bấm **Load**.
4.  Chọn Data Source là Prometheus vừa thêm.
5.  Bấm **Import**.

### Bước 3: Đọc các chỉ số lưu lượng (Traffic)
Trên Dashboard, các chỉ số sau thể hiện lưu lượng và tải của Database:

*   **Transactions / Sec (TPS)**: Số lượng giao dịch (commit/rollback) mỗi giây. Chỉ số này càng cao nghĩa là DB đang xử lý càng nhiều yêu cầu.
*   **Active Connections**: Số lượng kết nối đang hoạt động. Cần theo dõi để tránh hết slot kết nối.
*   **Tuples Fetched / Returned**: Lưu lượng đọc dữ liệu (Read traffic).
*   **Tuples Inserted / Updated / Deleted**: Lưu lượng ghi/thay đổi dữ liệu (Write traffic).

### Bước 4: Query thủ công (Nâng cao)
Bạn có thể tự vẽ biểu đồ bằng các câu query PromQL:

*   **Tổng Transaction/s:**
    ```promql
    sum(rate(pg_stat_database_xact_commit[1m])) + sum(rate(pg_stat_database_xact_rollback[1m]))
    ```
*   **Lưu lượng theo từng Database:**
    ```promql
    rate(pg_stat_database_xact_commit{datname="todo_app_development"}[1m])
    ```
