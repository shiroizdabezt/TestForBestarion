# Markdown Convert

## Introduction

File này mô tả các quy tắc về syntax, đặt tên biến, ... trong quy trinh chuyển đổi tài liệu **Markdown** sang file **.pdf** đúng quy chuẩn tài liêu hóa văn bản của **BESTARION**.

Mục tiêu chính của quy trình này nhằm để tăng tốc độ viết tài liệu của các thành viên trong team, cho phép tập trung tài liệu hóa các quy trình quan trọng dưới dạng **.md** thô, sau đó chuyển đổi sang file **.pdf** hoàn chỉnh mà không mất thời gian chuẩn hóa tài liệu.

## Rules

Phần này mô tả cụ thể các quy tắc, tiêu chuẩn và các bước trong quy trình chuyển đổi file **.md** sang **.pdf**.

Hiện tại, các tiêu chuẩn sau đây sẽ được đảm bảo khi thực hiện chuyển đổi:

### 1. Định dạng trang

- **Body Text**

  - Align Justified.
  - Font: Arial, 10pt, Black.

- **Margin**
  
  - Right, Left, Top, Bottom: 0.59 inch
  - Header, Footer: 0.30 inch
  
- **Layout**: A4 Paper.

### 3. Định dạng bảng

Project hỗ trợ render cấu trúc bảng trong file **.md** sang định dạng bảng đúng quy chuẩn được quy định.

Hiện tại, project chỉ hỗ trợ render cấu trúc bảng đơn giản như dưới đây:

```md
| Table header 1 | Table header 2 | Table header 3 |
|:---------------|:--------------:|---------------:| 
| Row 1, Col 1   |     Column 2   |    Column 3    |
| **Row 1 Col 2**  | *Row 1 Col 2*  | **_Row 1 Col 3_** |

: This is caption for above table
```

Bảng render ra tương ứng được áp dụng các style sau đây:

- Table Heading:

  - **Border**: 1 pt black.
  - **Font**: Arial bold.
  - **Size**: 10 pt.
  - **Color**: Black.
  - **Background color**: RGB (245, 130, 32).

- Table Content:
  
  - **Border**: 1 pt black.
  - **Font**: Arial regular.
  - **Size**: 10 pt.
  - **Color**: Black.

- Table caption:

  - **Font**: Arial italic.
  - **Size**: 9pt.
  - **Align**: Center
  - **Color**: Black.

- Table Align: Center

![example table](../images/image-2.png)

### 4. Định dạng hình ảnh

 Cách để thêm một ảnh vào file markdown:
```md
![this is image caption](./image.png)
```
Bên trong `![]` là phần caption của ảnh. Bên trong `()` là đường dẫn tới file ảnh.  

Hình ảnh render ra sẽ được áp dụng các style sau đây:
- Image caption:

  - **Font**: Arial italic.
  - **Size**: 9pt.
  - **Align**: Center
  - **Color**: Black.

- Image Align: Center

### 5. Định dạng văn bản

| Markdown Syntax | Style Convention | Mô tả |
| :-- | :-- | :-- |
| `#` | Heading 1 | **Font**: Arial, Bold<br>**Size**: 12<br>**Align**: Left<br>**Color**: Black<br>**Background color**: (RGB: 245, 130, 32) |
| `##` | Heading 2 | **Font**: Arial, Bold<br>**Size**: 11<br>**Align**: Left<br>**Color**: Black |
| `###` | Heading 3 | **Font**: Arial, Bold<br>**Size**: 10<br>**Align**: Left<br>**Color**: Black |
| `- <List Item>` | List 1 | **Font**: Arial, Regular<br>**Size**: 10<br>**Align**: Left<br>**Character**: `●` |
| `__ - <List Item 2>` | List 2 | **Font**: Arial, Regular<br>**Size**: 10<br>**Align**: Left<br>**Character**: `■` |
| `____ - <List Item 3>` | List 3 | **Font**: Arial, Regular<br>**Size**: 10<br>**Align**: Justified<br>**Character**: `•` |
| Table Content | Table Content | **Border**: 1pt black<br>**Font**: Arial, Regular<br>**Size**: 10<br>**Align**: Left<br>**Color**: Black |
| Table Heading | Table Heading 1 | **Border**: 1pt black<br>**Font**: Arial, Bold<br>**Size**: 10<br>**Align**: Left<br>**Color**: Black<br>**Background color**: (RGB: 245, 130, 32) |
| `: caption` | Table  caption |  **Font**: Arial, Italic<br>**Size**: 9<br>**Align**: Center<br>**Color**: Black |
| `![caption text](./image.png)` | Image |  **Font**: Arial, Italic<br>**Size**: 9<br>**Align**: Center<br>**Color**: Black |
|  | Image caption |  **Font**: Arial, Italic<br>**Size**: 9<br>**Align**: Center<br>**Color**: Black |


### 4. Metadata Variables

File **.md** cần được chuyển đổi cần có một yaml field đặc biệt được định nghĩa bằng hai dấu `---` ở đầu file, mục tiêu của block yaml này nhằm để định nghĩa các **metadata variables** sẽ được LaTeX engine sử dụng để render các trang khởi đầu trong file **.pdf** đầu ra.

Các trang khởi đầu sẽ được generate dựa vào metadata variable bao gồm:

- Cover page.
- Document control.

Dưới đây là ví dụ một block yaml:

```yaml
---
varriable_1: "value 1"
varriable_2: "value 2"
...
---

<!-- Nội dung cụ thể của fie markdown -->
```

Quy trình chuyển đổi Markdown → PDF gồm 3 bước chính như sau:

- Bước 1: Pandoc đọc file Markdown và trích xuất YAML metadata.
- Bước 2: Pandoc thay thế các variables trong LaTeX template bằng giá trị từ metadata.
- LaTeX engine (xelatex) render template kết hợp với filer bằng script custom Lua thành file PDF cuối cùng.

Các variables được chia thành 2 loại chính như sau:

- **Pandoc standard**: là các variables được Pandoc định nghĩa sẵn như title, author, date, etc.
- **Custom Variables**: là các variables do tài liệu tự định nghĩa như doc_code, updated_by, etc.

**1) Pandoc Standard Variables**

| Variables | Type | Description | Ví dụ |
| :-------- | :--- | :----------- | :------ |
| title | String | Document Name  | ORGANIZATIONAL PROCESSFOCUS PROCESS | 
| subtitle | String | Document Type Code | MSS |
| security | String | Security Classification | INTERNAL |
| version | String | Document Version | 1.0 |

**2) Pandoc Custom Variables**

| Variables | Type | Description | Ví dụ |
| :-------- | :--- | :----------- | :------ |
| template_id | String | Template ID  | ODT\_Base\_Template | 
| header_line | String | Header content | Bestarion.QM.CON.001-Controlled\_Document\_Convention-2\_1 |
| security | String | Security Classification | INTERNAL |
| version | String | Document Version | 1.0 |
| updated_by | String | Người update cuối cùng | Tuan Nguyen-Quoc-Anh |
| effective_date | String | Ngày update cuối cùng | Dec 08, 2025 | 
| changelog | Array[Object] | Danh sách object **change log** | |

**3) Object: change log**

| Variables | Type | Description | Ví dụ |
| :-------- | :--- | :----------- | :------ |
| version | String | Version update  | 1.0 | 
| description | String | Description about changes | Update content section 4. Document Convention |
| changed_by | String | Người thực hiện thay đổi | Tuan Nguyen-Quoc-Anh |
| changed_date | String | Ngày thay đổi | 2025-02-10 |
| approved | String | Người approved thay đổi | N/A |
| approval_date | String | Ngày approve | N/A | 

Đối với các quy tắc đặt tên ở **1)**, **2)** và **3)**, xin hãy tuân thủ các quy tắc được quy định trong tài liệu dưới đây:

- Bestarion.QM.CON.001-Controlled_Document_Convention-2_1.pdf

- Bestarion.QM.CON.004-Document_Responsibility_Authority_Standard-1_5.pdf

Dưới đây là ví dụ về một block yaml hoàn chỉnh:

```yaml
---
title: "ORGANIZATIONAL PROCESS FOCUS PROCESS"
subtitle: "MSS"
security: "INTERNAL"
doc_code: "Bestarion.MSS.PRO.015"
updated_by: "Tuan Nguyen-Quoc-Anh"
effective_date: "Dec 08, 2025"
version: "1.0"
template_id: "ODT_Base\\_Template"
header_line: "Bestarion.MSS.PRO.015-Organizational\\_Process\\_Focus-1\\_0"
changelog:
  - version: "1.0"
    description: "Tạo mới quy trình tập trung vào process của tổ chức."
    changed_by: "TuanNQA"
    changed_date: "2025-12-08"
    approved: "N/A"
    approval_date: "N/A"
  - version: "1.1"
    description: "Bổ sung section 4.2 về metrics và KPIs."
    changed_by: "AnhLT"
    changed_date: "2025-12-15"
    approved: "CTO/HungPV"
    approval_date: "2025-12-20"
  - version: "1.2"
    description: "Cập nhật template và format theo chuẩn mới."
    changed_by: "MaiNTH"
    changed_date: "2025-12-25"
    approved: "N/A"
    approval_date: "N/A"
---
```

Nội dung hai trang khởi đầu tương ứng sẽ được tạo ra như sau:

![Demo generate 2 pages](../images/image-1.png)

## Convention

Phần này mô tả một số quy tắc cần tuân thủ khi viết file **.md** để có thể chuyển đổi thành **.pdf** với LaTeX mà không gặp lỗi.

### 1. Tách biệt rõ ràng giữa các cú pháp markdown

Các thành phần trong file markdown phải được tách biệt rõ ràng bằng cách:

- Để trống một dòng giữa các phần tử khác nhau.
- Hoặc sử dụng hai dấu cách khi các phần tử cần nằm cạnh nhau.

Điều này giúp LaTeX nhận diện chính xác từng phần tử và áp dụng đúng định dạng.

**Cách viết SAI:**

```md
Nội dung text phía trên.
- List 1
  - List Item 1.1
  - List Item 1.2
      - List Item 1.2.1
- List 2
- List 3
Nội dung text phía dưới.
```

*Vấn đề:* LaTeX sẽ hiểu nhầm và gộp text với list thành một khối, làm mất định dạng.

**Cách viết ĐÚNG:**

```md
Nội dung text phía trên.

- List 1

  - List Item 1.1
  - List Item 1.2

      - List Item 1.2.1

- List 2
- List 3

Nội dung text phía dưới.
```

*Kết quả:* LaTeX sẽ render 3 phần tử riêng biệt: text trên → danh sách → cấp danh sách nhỏ hơn → text dưới.

### 2. Không sử dụng trùng lặp caption với ảnh

Bảng không mặc định có caption, vì vậy Pandoc hỗ trợ thêm cú pháp riêng để tạo caption là `: caption`. 

Trong khi đó đối với hình ảnh trong file markdown, Pandoc sẽ tự động trích xuất nội dung alt text để làm figure caption cho ảnh.

LaTeX sẽ dựa vào sự xuất hiện của các phần tử này + caption của chúng để cập nhật nội dung cho hai trang tương ứng là **Index of Table** và **Index of Figures**.

Việc sử dụng kết hợp cả `: caption` vào trong alt text của ảnh sẽ khiến LaTeX bị lẫn lộn cú pháp và không render chính xác nội dung hai trang này.

**Cách viết SAI:**

```md

![this alt text should be used as image's figure caption, but it does not](image.png)

: this caption will be used as image's figure.
```

*Kết quả:* LaTeX vẫn sẽ cố gắng sử dụng caption bên dưới làm figure caption cho ảnh trên, nhưng entry tương ứng của ảnh này trong bảng **Index of figures** sẽ không được cập nhật.

**Cách viết ĐÚNG:**

```md

![this alt text will be used as image's figure caption](image.png)
```

*Kết quả:* LaTeX sẽ render ra ảnh cùng với caption tương ứng được định nghĩa trong phần alt text, cũng như cập nhật lại entry của bảng **Index of figures**.

