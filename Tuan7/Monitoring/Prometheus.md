# Prometheus

- Prometheus là một bộ công cụ giám sát và cảnh báo mã nguồn mở. Nó thu thập các số liệu (metrics) từ các mục tiêu đã được cấu hình theo các khoảng thời gian nhất định, đánh giá các biểu thức quy tắc, hiển thị kết quả và có thể kích hoạt cảnh báo nếu một số điều kiện được đáp ứng.
- Prometheus lưu trữ dữ liệu dưới dạng time series (chuỗi thời gian), tức là các metrics được lưu trữ cùng với mốc thời gian mà chúng được ghi lại.

## Cách cấu hình Prometheus trong docker compose

Trong file `docker-compose.yml`, service `prometheus` được cấu hình như sau:

```yaml
  prometheus:
    image: prom/prometheus:latest
    container_name: monitoring_prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.retention.time=15d'
    ports:
      - "9090:9090"
    restart: always
```

### Giải thích các config trong docker compose:
- `image`: Sử dụng image chính thức của Prometheus từ Docker Hub (`prom/prometheus:latest`).
- `volumes`:
    - `./prometheus.yml:/etc/prometheus/prometheus.yml`: Mount file cấu hình từ máy host vào container để Prometheus có thể đọc được cấu hình scrape targets.
    - `prometheus_data:/prometheus`: Mount volume để lưu trữ dữ liệu metrics lâu dài, tránh mất dữ liệu khi container bị xóa.
- `command`:
    - `--config.file=/etc/prometheus/prometheus.yml`: Chỉ định đường dẫn đến file cấu hình.
    - `--storage.tsdb.retention.time=15d`: Cấu hình thời gian lưu trữ dữ liệu là 15 ngày. Sau thời gian này, dữ liệu cũ sẽ bị xóa.
- `ports`: Map port 9090 của container ra port 9090 của máy host (Port mặc định của Prometheus).

## Cấu hình file prometheus.yml

File `prometheus.yml` chứa các cấu hình về việc scrape (thu thập) dữ liệu từ đâu và như thế nào.

```yaml
global:
  scrape_interval: 10s

scrape_configs:
  # JOB 1: Giám sát Máy Controller (172.16.4.15)
  - job_name: 'controller_machine'
    static_configs:
      - targets: 
          - 'node-exporter:9100'      # CPU/RAM
          - 'postgres-exporter:9187'  # Database Postgres
        labels:
          role: 'monitoring-server'
          db: 'primary'

  # JOB 2: Giám sát Máy Ảo (192.168.122.143)
  - job_name: 'virtual_machine_1'
    static_configs:
      # Chỉ lấy Web Metrics (Node Exporter + Script)
      - targets: ['192.168.122.143:9100']
        labels:
          env: 'production'
          app: 'rails_passenger'
```

### Giải thích các config trong prometheus.yml:
- `global`: Các cấu hình chung cho toàn bộ Prometheus.
    - `scrape_interval`: Khoảng thời gian giữa các lần thu thập dữ liệu (ở đây là 10 giây).
- `scrape_configs`: Danh sách các job để thu thập dữ liệu.
    - `job_name`: Tên định danh cho job (ví dụ: `controller_machine`, `virtual_machine_1`). Tên này sẽ được gắn vào label `job` của các metrics thu thập được.
    - `static_configs`: Cấu hình tĩnh các target cần scrape.
        - `targets`: Danh sách các địa chỉ (host:port) để Prometheus kết nối và lấy metrics. Ví dụ: `node-exporter:9100` (lấy metrics hệ thống), `postgres-exporter:9187` (lấy metrics database).
        - `labels`: Các nhãn tùy chỉnh được gắn thêm vào tất cả các metrics thu thập được từ target này. Ví dụ: `role: monitoring-server`, `env: production`. Điều này rất hữu ích khi query và filter dữ liệu trong Grafana.

## Các flags quan trọng và danh sách flags của Prometheus

Prometheus server có rất nhiều flags (cờ) dòng lệnh để cấu hình hành vi của nó khi khởi động. Các flags này thường được thêm vào phần `command` trong docker-compose hoặc khi chạy trực tiếp binary.

### Các flags quan trọng thường dùng

1.  **`--config.file`**:
    -   **Mô tả**: Đường dẫn đến file cấu hình chính của Prometheus (thường là `prometheus.yml`).
    -   **Mặc định**: `prometheus.yml`
    -   **Ví dụ**: `--config.file=/etc/prometheus/prometheus.yml`

2.  **`--storage.tsdb.retention.time`**:
    -   **Mô tả**: Thời gian lưu trữ dữ liệu. Dữ liệu cũ hơn khoảng thời gian này sẽ bị xóa.
    -   **Mặc định**: `15d` (15 ngày).
    -   **Ví dụ**: `--storage.tsdb.retention.time=30d` (Lưu 30 ngày), `--storage.tsdb.retention.time=1y` (Lưu 1 năm).

3.  **`--storage.tsdb.path`**:
    -   **Mô tả**: Thư mục nơi Prometheus lưu trữ cơ sở dữ liệu time-series.
    -   **Mặc định**: `data/`
    -   **Ví dụ**: `--storage.tsdb.path=/prometheus`

4.  **`--web.listen-address`**:
    -   **Mô tả**: Địa chỉ và cổng mà Prometheus sẽ lắng nghe các request HTTP (UI, API, và metrics của chính nó).
    -   **Mặc định**: `0.0.0.0:9090`
    -   **Ví dụ**: `--web.listen-address=:9090`

5.  **`--web.enable-lifecycle`**:
    -   **Mô tả**: Cho phép reload cấu hình hoặc tắt Prometheus thông qua HTTP API (POST `/-/reload` hoặc `/-/quit`). Rất hữu ích để reload config mà không cần restart container.
    -   **Mặc định**: `false` (Tắt).
    -   **Ví dụ**: `--web.enable-lifecycle`

### Danh sách các flags khác

Dưới đây là danh sách các flags phổ biến khác của Prometheus:

-   `--web.external-url=<URL>`: URL bên ngoài mà người dùng truy cập vào Prometheus (cần thiết nếu chạy sau reverse proxy).
-   `--web.route-prefix=<path>`: Prefix cho các đường dẫn nội bộ của Prometheus.
-   `--log.level=<level>`: Mức độ log (debug, info, warn, error). Mặc định là `info`.
-   `--log.format=<format>`: Định dạng log (`logfmt` hoặc `json`).
-   `--storage.tsdb.retention.size=<size>`: Giới hạn dung lượng lưu trữ tối đa (ví dụ `512MB`, `10GB`). Nếu vượt quá, dữ liệu cũ nhất sẽ bị xóa. Có thể dùng chung với `retention.time` (cái nào đến trước thì áp dụng).
-   `--storage.tsdb.wal-compression`: Bật nén Write-Ahead Log (WAL) để tiết kiệm dung lượng đĩa.
-   `--storage.tsdb.no-lockfile`: Không tạo lockfile trong thư mục data (thường dùng trong môi trường container đặc biệt).
-   `--query.timeout=<duration>`: Thời gian tối đa cho một câu truy vấn trước khi bị hủy. Mặc định `2m`.
-   `--query.max-concurrency=<int>`: Số lượng truy vấn tối đa được thực hiện đồng thời. Mặc định `20`.
-   `--query.max-samples=<int>`: Số lượng mẫu tối đa mà một truy vấn có thể load vào bộ nhớ. Mặc định `50000000`.
-   `--alertmanager.notification-queue-capacity=<int>`: Dung lượng hàng đợi cho các cảnh báo đang chờ gửi. Mặc định `10000`.
-   `--alertmanager.timeout=<duration>`: Thời gian chờ khi gửi cảnh báo đến Alertmanager. Mặc định `10s`.
-   `--enable-feature=remote-write-receiver`: Cho phép Prometheus nhận dữ liệu từ các Prometheus khác qua remote write (dùng như một nơi tập trung metrics).
> **Lưu ý**: Để xem toàn bộ danh sách flags của phiên bản hiện tại, có thể chạy lệnh:
> `docker run --rm prom/prometheus --help`

## Hình ảnh và cách sử dụng Prometheus
- Sau khi chạy docker compose, truy cập vào `http://localhost:9090` để vào giao diện Prometheus.
    ![Prometheus home screen](./images/Prometheus_home_screen.png)
- **Giao diện Targets**: Kiểm tra xem Prometheus đã kết nối thành công đến các exporter chưa.
    - Vào menu **Status** -> **Targets**.
    - Nếu State là **UP** màu xanh lá cây nghĩa là đã kết nối thành công.
    ![Prometheus Targets Status](./images/Prometheus_targets_status.png)

- **Giao diện Graph**: Dùng để chạy các câu query PromQL và xem biểu đồ dữ liệu thô.
    - Nhập query vào ô Expression (ví dụ: `up`, `node_cpu_seconds_total`).
    - Nhấn **Execute**.
    - Chuyển qua tab **Graph** để xem biểu đồ.
    ![Prometheus Graph UI](./images/Prometheus_graph_ui.png)

- **Giao diện Service Discovery**: Xem danh sách các target được phát hiện (nếu dùng cơ chế dynamic discovery).
    - Vào menu **Status** -> **Service Discovery**.
    ![Prometheus Service Discovery](./images/Prometheus_service_discovery.png)

## Kiến thức về PromQL và các câu query quan trọng

PromQL (Prometheus Query Language) là ngôn ngữ truy vấn mạnh mẽ của Prometheus. Dưới đây là các khái niệm cơ bản và các câu query mẫu cho các exporter phổ biến.

### 1. Các khái niệm cơ bản

*   **Instant Vector**: Một tập hợp các time series, mỗi cái có một giá trị duy nhất tại cùng một thời điểm.
    *   Ví dụ: `node_cpu_seconds_total`
*   **Range Vector**: Một tập hợp các time series, mỗi cái chứa một loạt các điểm dữ liệu theo thời gian.
    *   Ví dụ: `node_cpu_seconds_total[5m]` (Lấy dữ liệu trong 5 phút qua)
*   **Functions**: Các hàm xử lý dữ liệu.
    *   `rate()`: Tính tốc độ trung bình trên giây (dùng cho Counter).
    *   `irate()`: Tính tốc độ tức thời (dùng cho Counter biến động nhanh).
    *   `increase()`: Tính lượng tăng lên trong khoảng thời gian.
    *   `sum()`, `avg()`, `min()`, `max()`: Các hàm tổng hợp.

### 2. Node Exporter Queries (Giám sát Server)

Dùng để giám sát tài nguyên hệ thống như CPU, RAM, Disk, Network.

*   **CPU Usage (%)**: Tính phần trăm CPU đang được sử dụng (không tính idle).
    ```promql
    100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)
    ```

*   **Memory Usage (%)**: Tính phần trăm RAM đã sử dụng.
    ```promql
    100 * (1 - ((node_memory_MemFree_bytes + node_memory_Buffers_bytes + node_memory_Cached_bytes) / node_memory_MemTotal_bytes))
    ```

*   **Disk Space Usage (%)**: Tính phần trăm ổ cứng đã đầy.
    ```promql
    100 - ((node_filesystem_avail_bytes * 100) / node_filesystem_size_bytes)
    ```

*   **Network Traffic (Receive/Transmit)**: Lưu lượng mạng vào/ra (bytes/sec).
    ```promql
    rate(node_network_receive_bytes_total[5m])  # Download
    rate(node_network_transmit_bytes_total[5m]) # Upload
    ```

### 3. Postgres Exporter Queries (Giám sát Database)

Dùng để giám sát hiệu năng và trạng thái của PostgreSQL.

*   **Active Connections**: Số lượng kết nối đang hoạt động.
    ```promql
    sum(pg_stat_activity_count{state="active"}) by (datname)
    ```

*   **Transactions per Second (TPS)**: Số lượng giao dịch (commit + rollback) mỗi giây.
    ```promql
    sum(rate(pg_stat_database_xact_commit[5m])) + sum(rate(pg_stat_database_xact_rollback[5m]))
    ```

*   **Cache Hit Ratio (%)**: Tỷ lệ đọc dữ liệu từ RAM (Cache) so với đọc từ Ổ cứng (Disk). Tốt nhất nên > 99%.
    ```promql
    sum(pg_stat_database_blks_hit) / (sum(pg_stat_database_blks_hit) + sum(pg_stat_database_blks_read)) * 100
    ```

*   **Database Size**: Kích thước của từng database.
    ```promql
    pg_database_size_bytes
    ```

### 4. Blackbox Exporter Queries (Giám sát Website/Endpoint)

Dùng để kiểm tra tính sẵn sàng (Uptime), tốc độ phản hồi, và chứng chỉ SSL của website.

*   **Probe Success**: Kiểm tra xem website có sống hay không (1 = Up, 0 = Down).
    ```promql
    probe_success
    ```

*   **HTTP Status Code**: Mã phản hồi HTTP (200, 404, 500...).
    ```promql
    probe_http_status_code
    ```

*   **Probe Duration**: Thời gian phản hồi của request (giây).
    ```promql
    probe_duration_seconds
    ```

*   **SSL Certificate Expiry**: Thời gian còn lại của chứng chỉ SSL (giây).
    ```promql
    probe_ssl_earliest_cert_expiry - time()
    ```
    *   Để cảnh báo nếu SSL sắp hết hạn trong 7 ngày: `(probe_ssl_earliest_cert_expiry - time()) < 86400 * 7`
