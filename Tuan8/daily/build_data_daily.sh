tmp9 = -A | grep -v ^abc | grep -i error
#Có tác dụng tìm kiếm, đọc các file log để timg ra lỗi và bỏ qua tất cả các dòng không phải lỗi `abc`

#Nếu Database là all
#Ta sẽ lấy list_priority_id từ trong database ra để tiến hành chạy
list_priority_id=($(psql -tAU postgres -p 6432 \
                    -h "$mainhost" "devops" -c "SELECT STRING_AGG(org_id::TEXT, ' ') \
                    AS org_id FROM list_priority_build WHERE ${BASE_NAME}=TRUE"))

# Nếu mảng rỗng thì ta sẽ gán order by = organizatios.id
if [ ${#list_priority_id[@]} -eq 0 ]; then
    order_by="organizations.id"
else
    order_by=$(printf "organizations.id = %s DESC, " "${list_priority_id[@]}")
    order_by="${order_by}"  # Xóa dấu phẩy cuối cùng
fi

#Nếu Database không phải là all
#Ta sẽ gán vào biến database và thay thế các ký tự _ thành ,
database="${DATABASE//_/,}"
orgs=($(psql -tAU postgres -p 6432 -h "$mainhost" "$maindb" -c "SELECT id, db_name, db_ipaddress FROM organizations WHERE id IN ($database) ORDER BY id"))

#Các hàm để khai báo queue
function queue {
  QUEUE="$QUEUE $1"
  NUM=$((NUM+1))
}

function regeneratequeue {
  OLDREQUEUE=$QUEUE
  QUEUE=""
  NUM=0
  for PID in $OLDREQUEUE
  do
    if [ -d /proc/"$PID"  ] ; then
      QUEUE="$QUEUE $PID"
      NUM=$((NUM+1))
    fi
  done
}

function checkqueue {
  OLDCHQUEUE=$QUEUE
  for PID in $OLDCHQUEUE
  do
    if [ ! -d /proc/"$PID" ] ; then
      regeneratequeue # at least one PID has finished
      break
    fi
  done
}

# Viết func để build
function build {
  local org_id=a
  local db=b
  local steps=("stepa" "stepb" "stepc" "stepd")
  
  puts "Build org $org_id - $db begin"
  cd CURRENT_DIR

  for step in "${steps[@]}"; do

    puts "Building $step for org $org_id - $db"
    ./build_data.sh -d "$db" -j "$step" 

  done

  cd CURRENT_DIR/../
  puts "Build org id - $db end"
}

puts "Using $MAX_NPROC parallel threads"

LOG_FOLDER=CURRENT_DIR/../log/build_data_daily/
mkdir -p LOG_FOLDER

for org in ${orgs[@]}
do
  org_id=a
  db=b
  host=c

  build "$org_id" "$db" &

  PID=$!
  queue $PID

  while [ $NUM -ge $MAX_NPROC ]; do
    checkqueue
    sleep 0.4
  done
done







solr contract 7.7.2
solr vim 6.6.6