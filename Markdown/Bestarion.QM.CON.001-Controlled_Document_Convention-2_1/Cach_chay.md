- Trên máy chạy jenkins để convert phải cài đặt sẵn font arial với lệnh
```bash
sudo apt-get update
sudo apt-get install ttf-mscorefonts-installer -y
sudo fc-cache -f -v
```
- Sau đó mới chạy lệnh convert:
```bash
pandoc Bestarion.QM.CON.001-Controlled_Document_Convention-2_1.md -o template.pdf --template=template.tex --pdf-engine=xelatex --resource-path=.
```