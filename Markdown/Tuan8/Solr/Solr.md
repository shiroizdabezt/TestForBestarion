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