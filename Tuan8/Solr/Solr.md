# Solr
- Là một máy chủ mã nguồn mở được xây dựng trên Apache Lucene, cung cấp khả năng tìm kiếm và lập chỉ mục mạnh mẽ cho các ứng dụng web.
- Solr hoạt động độc lập và nhiệm vụ của nó là: 
  - Nhận các yêu cầu tìm kiếm từ ứng dụng web.
  - Tìm kiếm trong chỉ mục dữ liệu đã được lập chỉ mục trước đó.
  - Trả về kết quả tìm kiếm cho ứng dụng web.
- Solr hỗ trợ nhiều tính năng như phân tích ngôn ngữ tự nhiên, phân tách từ khóa, hỗ trợ đa ngôn ngữ, và khả năng mở rộng linh hoạt.

## Quy trình hoạt động
1. **Thu thập dữ liệu (Data Ingestion):** Dữ liệu từ các nguồn khác nhau được thu thập và chuẩn bị để lập chỉ mục.
2. **Lập chỉ mục (Indexing):** Dữ liệu thu thập được được chuyển đổi thành định dạng phù hợp và lập chỉ mục trong Solr để tối ưu hóa việc tìm kiếm.
3. **Tìm kiếm (Searching):** Khi người dùng gửi yêu cầu tìm kiếm, Solr sẽ truy vấn chỉ mục và trả về kết quả phù hợp.
4. **Phân tích và xếp hạng (Analysis and Ranking):** Solr sử dụng các thuật toán phân tích và xếp hạng để đảm bảo rằng kết quả tìm kiếm là chính xác và liên quan nhất.
5. **Trả về kết quả (Result Delivery):** Kết quả tìm kiếm được trả về cho ứng dụng web để hiển thị cho người dùng.

## So sánh Solr với tìm kiếm Database truyền thống
| Tiêu chí               | Tìm kiếm Database truyền thống | Solr                              |
|-----------------------|--------------------------------|----------------------------------|
| Hiệu suất             | Thường chậm hơn với dữ liệu lớn | Tối ưu hóa cho tìm kiếm nhanh chóng |
| Khả năng mở rộng      | Hạn chế trong việc mở rộng       | Dễ dàng mở rộng theo nhu cầu     |
| Tính năng tìm kiếm    | Cơ bản, hạn chế                 | Nhiều tính năng nâng cao         |
| Hỗ trợ ngôn ngữ tự nhiên | Thường không hỗ trợ tốt          | Hỗ trợ mạnh mẽ                   | 
| Tùy chỉnh             | Hạn chế                         | Linh hoạt và dễ dàng tùy chỉnh   |

## Các loại Solr
- **Standalone Solr:** Chạy trên một máy chủ duy nhất, phù hợp cho các ứng dụng nhỏ hoặc thử nghiệm.
- **SolrCloud:** Một cấu hình phân tán của Solr, cho phép mở rộng quy mô và cung cấp khả năng chịu lỗi cao hơn bằng cách sử dụng nhiều nút (nodes) trong một cụm (cluster).

# Zookeeper
- Zookeeper là một dịch vụ quản lý cấu hình và đồng bộ hóa dữ liệu trong SolrCloud.
- Zookeeper giúp các nút trong cụm SolrCloud đồng bộ hóa với nhau và đảm bảo tính nhất quán trong quá trình tìm kiếm và lập chỉ mục.

## Cách cài đặt Solr
### Cài đặt Zookeeper
- Cách 1: Cài đặt Zookeeper theo hướng dẫn chính thức của Apache Zookeeper.
- Cách 2: Cài đặt Zookeeper thông qua Docker.

### Cài đặt Solr
- Cách 1: Cài đặt Solr theo hướng dẫn chính thức của Apache Solr.
- Cách 2: Cài đặt Solr thông qua Docker.

```yaml
version: '3.8'

services:
  # --- Service Database (Giữ nguyên) ---
  khoa_postgre:
    image: postgres:14
    container_name: khoa_postgre
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: main_production47
    volumes:
      - ./pg_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - pg_network

  # --- Service PgBouncer (Giữ nguyên) ---
  khoa_pgbouncer:
    image: edoburu/pgbouncer:latest
    container_name: khoa_pgbouncer
    restart: always
    depends_on:
      - khoa_postgre
    volumes:
      - ./pgbouncer/pgbouncer.ini:/etc/pgbouncer/pgbouncer.ini
      - ./pgbouncer/userlist.txt:/etc/pgbouncer/userlist.txt
    ports:
      - "7432:7432"
    networks:
      - pg_network

  # ======================================================
  # CỤM 1: SOLR 6.6.6 + ZOOKEEPER RIÊNG
  # ======================================================
  
  # Zookeeper cho Solr 6
  zookeeper1:
    image: zookeeper:3.4.14
    container_name: khoa_zookeeper_1
    restart: always
    ports:
      - "22181:2181" # Port Host: 22181
    environment:
      ZOO_MY_ID: 1
    networks:
      - pg_network

  # Solr 6.6.6
  solr6:
    image: solr:6.6.6
    container_name: khoa_solr6
    restart: always
    ports:
      - "28981:8983" # Port Host: 28981
    environment:
      - ZK_HOST=khoa_zookeeper_1:2181 # Trỏ đích danh vào container khoa_zookeeper_1
    depends_on:
      - zookeeper1
    networks:
      - pg_network
    volumes:
      - solr6_data:/var/solr

  # ======================================================
  # CỤM 2: SOLR 7.7.2 + ZOOKEEPER RIÊNG
  # ======================================================

  # Zookeeper cho Solr 7
  zookeeper2:
    image: zookeeper:3.4.14
    container_name: khoa_zookeeper_2
    restart: always
    ports:
      - "22182:2181" # Port Host: 22182
    environment:
      ZOO_MY_ID: 1
    networks:
      - pg_network

  # Solr 7.7.2
  solr7:
    image: solr:7.7.2
    container_name: khoa_solr7
    restart: always
    ports:
      - "28983:8983" # Port Host: 28983
    environment:
      - ZK_HOST=khoa_zookeeper_2:2181 # Trỏ đích danh vào container khoa_zookeeper_2
    depends_on:
      - zookeeper2
    networks:
      - pg_network
    volumes:
      - solr7_data:/var/solr
      #- ./my-cloud-scripts:/opt/solr/server/scripts/cloud-scripts

networks:
  pg_network:
    driver: bridge

volumes:
  solr6_data:
  solr7_data:
```

/home/khoa/msss/app/controllers/new_organization_vims_controller.rb 
/home/khoa/msss/app/views/new_organization_vims/index.html.erb 
/home/khoa/msss/app/controllers/org/organization_vims_controller.rb 
/home/khoa/msss/app/models/services/org/vim/main_service.rb 
/home/khoa/msss/public/javascripts/meperia/vim/vim-item-panel.js