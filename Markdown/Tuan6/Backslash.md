## Backslash

| Notation          | Mô tả                                                              | Giá trị ASCII/Hex | Ví dụ & Kết quả                                                                  |
| ----------------- | ------------------------------------------------------------------ | ----------------- | -------------------------------------------------------------------------------- |
| `\n`              | **Newline** – Xuống dòng                                           | 0x0A              | `"Line1\nLine2"` → <br> `Line1` <br> `Line2`                                     |
| `\r`              | **Carriage Return** – Trở về đầu dòng                              | 0x0D              | `"Hello\rHi"` → `Hi` (ghi đè “Hello” bằng “Hi” trên cùng dòng trong terminal cũ) |
| `\f`              | **Form feed** – Chuyển trang (ít dùng, chủ yếu trong máy in)       | 0x0C              | `"Hello\fWorld"`                                                                 |
| `\b`              | **Backspace** – Lùi lại 1 ký tự                                    | 0x08              | `"abc\bz"` → in ra `abz`                                                         |
| `\a`              | **Bell / Alert** – Phát tiếng beep (trên terminal có hỗ trợ)       | 0x07              | `print "\a"` → kêu “beep”                                                        |
| `\e`              | **Escape** – Ký tự escape (dùng trong màu sắc terminal, ANSI code) | 0x1B              | `puts "\e[31mRed Text\e[0m"` → in chữ đỏ                                         |
| `\s`              | **Space** – Dấu cách (0x20)                                        | 0x20              | `"Hello\sWorld"` → `"Hello World"`                                               |
| `\nnn`            | **Octal notation** – Ký tự theo mã **bát phân** (n là 0–7)         | Octal             | `"\101"` → `"A"` (vì 101 trong octal = 65 trong ASCII = A)                       |
| `\xnn`            | **Hexadecimal notation** – Ký tự theo mã **hex** (0–9, a–f, A–F)   | Hexadecimal       | `"\x41"` → `"A"`                                                                 |
| `\cx` hoặc `\C-x` | **Control-x** – Tổ hợp phím điều khiển (`Ctrl + x`)                | Control           | `"\C-a"` → Ctrl + A (ASCII 0x01)                                                 |
| `\M-x`            | **Meta-x** – Thêm bit 8 (Meta bit)                                 | c | 0x80          | `"\M-a"` → ký tự có mã ASCII + 128                                               |
| `\M-\C-x`         | **Meta-Control-x** – Cả Meta và Control                            | —                 | Dùng cho tổ hợp Meta + Ctrl                                                      |
| `\x`              | **Ký tự x** (nếu `\` không theo cú pháp đặc biệt)                  | —                 | `"Hello\z"` → in `"Helloz"` (vì `\z` không có nghĩa đặc biệt)                    |

khasc owr daya nef