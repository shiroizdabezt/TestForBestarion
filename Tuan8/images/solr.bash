# Cách tìm ra lỗi
# Chạy strace, lọc các hành động ghi/đọc mạng, và tăng giới hạn hiển thị chuỗi lên 2000 ký tự để đọc trọn vẹn thông báo lỗi
strace -f -e trace=write,read -s 2000 script/vvsolr -b -d main_production47 -p 7432 -h 172.16.4.43 -oid 137 -U postgres -P postgres -input_table test_vsolr -solr_url http://172.16.4.15:28981/solr -solr_name VINHNH_LAWRENCE


# Chạy strace, nhưng lần này chúng ta chỉ cần nhìn output chuẩn (stderr/stdout) của chương trình, không cần trace sâu vào system call
# Thực tế lỗi này in ra log của ứng dụng, nên chỉ cần chạy bình thường và xem log.
# Tuy nhiên nếu muốn dùng strace để bắt chính xác thời điểm nó ghi lỗi ra màn hình:
strace -f -e trace=write -s 2000 script/vvsolr -b -d main_production47 -p 7432 -h 172.16.4.43 -oid 137 -U postgres -P postgres -input_table test_vsolr -solr_url http://172.16.4.15:28981/solr -solr_name VINHNH_LAWRENCE


# Cách sửa

# Backup file gốc
cp script/vvsolr script/vvsolr.bak
# Thay thế chuỗi ký tự (giữ nguyên độ dài bằng cách thêm 2 ký tự null)
perl -pi -e 's/extra_float_digits/application_name\x00\x00/g' script/vvsolr
# Sửa chỉ thị độ dài chuỗi từ 18 (0x12) thành 16 (0x10) tại offset 3168662
printf '\x10' | dd of=script/vvsolr bs=1 seek=3168662 count=1 conv=notrunc

PGPASSWORD=postgres psql -h 172.16.4.43 -p 7432 -U postgres -d org -c "ALTER TABLE master_items ADD COLUMN reference_numbers text;"