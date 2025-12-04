# How to run it:

pandoc README.md --template=convention.tex   --pdf-engine=xelatex -o convention.pdf --lua-filter=convention.lua