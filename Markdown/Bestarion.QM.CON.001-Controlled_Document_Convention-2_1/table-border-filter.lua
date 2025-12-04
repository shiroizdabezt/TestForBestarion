local color_map = {
    ['H1'] = 'brandOrange',
    ['H2'] = 'mediumOrange',
    ['H3'] = 'lightOrange'
}

-- Hàm chuyển đổi nội dung Pandoc (ví dụ: in đậm, link) sang chuỗi LaTeX an toàn
local function to_latex(inline_list)
    -- Sử dụng Pandoc writer là cách duy nhất để giữ lại formatting (bold, links)
    return pandoc.write(inline_list, 'latex') 
end

local function get_header_color(el)
    -- Nếu el.attr không tồn tại, trả về nil ngay lập tức
    if not el.attr then
        return nil
    end

    -- Kiểm tra thuộc tính Class của bảng để xác định màu
    for _, class in ipairs(el.attr.classes) do
        if color_map[class] then
            return color_map[class]
        end
    end
    return nil -- Trả về nil nếu không tìm thấy Class màu
end

-- Hàm này chỉ chạy khi Pandoc gặp một đối tượng bảng
function Table(el)
    local num_cols = 0
    local table_string = ""
    -- Mặc định màu là H1 nếu không có class nào được chỉ định
    local header_color = get_header_color(el) or 'brandOrange'
    
    -- 1. Xác định số lượng cột
    if el.head and #el.head > 0 and #el.head[1].cells > 0 then
        num_cols = #el.head[1].cells
    else
        return nil -- Nếu không có cột/header, bỏ qua và để Pandoc xử lý mặc định
    end

    -- 2. Tạo định dạng cột LaTeX với Full Border (Ví dụ: {|l|l|l|})
    local col_spec = "{"
    for i=1, num_cols do
        col_spec = col_spec .. "|l" -- Luôn căn lề trái (l) và thêm border (|)
    end
    col_spec = col_spec .. "|}" 

    -- 3. Bắt đầu môi trường longtable
    table_string = table_string .. "\\begin{longtable}" .. col_spec .. "\n"
    
    -- Lấy Caption và chèn vào
    local caption_text = to_latex(el.caption)
    table_string = table_string .. "\\caption{" .. caption_text .. "} \\\\ \\hline\n"
    
    -- === 4. Xây dựng Header và Body ===
    
    -- Lấy hàng tiêu đề (dùng cho cả endfirsthead và endhead)
    local header_cells = {}
    for _, cell in ipairs(el.head[1].cells) do
        table.insert(header_cells, "\\textbf{" .. to_latex(cell.content) .. "}")
    end
    local header_row_content = table.concat(header_cells, " & ")

    -- ENDFIRSTHEAD (Tiêu đề trang đầu tiên)
    table_string = table_string .. "\\rowcolor{" .. header_color .. "}\n" -- Thêm màu
    table_string = table_string .. header_row_content .. " \\\\ \n"
    table_string = table_string .. "\\hline\n"
    table_string = table_string .. "\\endfirsthead\n" 

    -- ENDHEAD (Tiêu đề lặp lại)
    -- Header lặp lại trên các trang
    table_string = table_string .. "\\multicolumn{" .. num_cols .. "}{c}%{{\\bfseries \\tablename\\ \\thetable{} -- Tiếp theo}} \\\\ \\hline\n"
    table_string = table_string .. "\\rowcolor{" .. header_color .. "}\n"
    table_string = table_string .. header_row_content .. " \\\\ \\hline\n"
    table_string = table_string .. "\\endhead\n"

    -- Nội dung Body (Các hàng dữ liệu)
    for _, body in ipairs(el.bodies) do
        for _, row in ipairs(body.rows) do
            local cells = {}
            for _, cell in ipairs(row.cells) do
                table.insert(cells, to_latex(cell.content))
            end
            table_string = table_string .. table.concat(cells, " & ") .. " \\\\ \\hline\n"
        end
    end
    
    -- 6. Kết thúc môi trường longtable
    table_string = table_string .. "\\end{longtable}\n"

    -- 7. Trả về khối Raw LaTeX đã được xây dựng thủ công
    return pandoc.RawBlock('latex', table_string)
end