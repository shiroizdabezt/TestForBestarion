-- ============================================================
-- Pandoc Lua Filter: Table Borders and Styling for LaTeX
-- Supports: alignment, bold, italic, nested formatting, caption
-- ============================================================

-- Configuration
local VERTICAL_PADDING = 1.5

-- Convert alignment enum to LaTeX column specifier
local function get_column_alignment(align)
  if align == pandoc.AlignLeft then
    return 'l'
  elseif align == pandoc.AlignRight then
    return 'r'
  elseif align == pandoc.AlignCenter then
    return 'c'
  else
    return 'l'
  end
end

-- Build LaTeX column specification with borders
local function build_column_spec(colspecs)
  local specs = {}
  for _, colspec in ipairs(colspecs) do
    local align = get_column_alignment(colspec[1])
    table.insert(specs, align)
  end
  return '|' .. table.concat(specs, '|') .. '|'
end

-- Recursively convert Pandoc inline elements to LaTeX
local function inlines_to_latex(inlines)
  local result = {}
  
  for _, inline in ipairs(inlines) do
    if inline.tag == 'Str' then
      local escaped = inline.text:gsub('([&%%$#_{}~^\\])', '\\%1')
      table.insert(result, escaped)
      
    elseif inline.tag == 'Space' then
      table.insert(result, ' ')
      
    elseif inline.tag == 'SoftBreak' or inline.tag == 'LineBreak' then
      table.insert(result, ' ')
      
    elseif inline.tag == 'Strong' then
      local content = inlines_to_latex(inline.content)
      table.insert(result, '\\textbf{' .. content .. '}')
      
    elseif inline.tag == 'Emph' then
      local content = inlines_to_latex(inline.content)
      table.insert(result, '\\textit{' .. content .. '}')
      
    elseif inline.tag == 'Code' then
      local escaped = inline.text:gsub('([&%%$#_{}~^\\])', '\\%1')
      table.insert(result, '\\texttt{' .. escaped .. '}')
      
    elseif inline.tag == 'Strikeout' then
      local content = inlines_to_latex(inline.content)
      table.insert(result, '\\sout{' .. content .. '}')
      
    elseif inline.tag == 'Superscript' then
      local content = inlines_to_latex(inline.content)
      table.insert(result, '\\textsuperscript{' .. content .. '}')
      
    elseif inline.tag == 'Subscript' then
      local content = inlines_to_latex(inline.content)
      table.insert(result, '\\textsubscript{' .. content .. '}')
      
    else
      table.insert(result, pandoc.utils.stringify(inline))
    end
  end
  
  return table.concat(result, '')
end

-- Extract and format content from table cell
local function process_table_cell(cell)
  local parts = {}
  
  for _, block in ipairs(cell.contents) do
    if block.tag == 'Plain' or block.tag == 'Para' then
      local latex_content = inlines_to_latex(block.content)
      table.insert(parts, latex_content)
    else
      table.insert(parts, pandoc.utils.stringify(block))
    end
  end
  
  return table.concat(parts, ' ')
end

-- Format header row with orange background
local function format_header_row(row)
  local cells = {}
  
  for _, cell in ipairs(row.cells) do
    local content = process_table_cell(cell)
    table.insert(cells, '\\textbf{' .. content .. '}')
  end
  
  local latex = [[
\rowcolor{brandOrange}]] .. table.concat(cells, ' & ') .. [[ \\
\hline]]
  
  return pandoc.RawBlock('latex', latex)
end

-- Format body row
local function format_body_row(row)
  local cells = {}
  
  for _, cell in ipairs(row.cells) do
    local content = process_table_cell(cell)
    table.insert(cells, content)
  end
  
  local latex = table.concat(cells, ' & ') .. [[ \\]]
  
  return pandoc.RawBlock('latex', latex)
end

-- Build table begin LaTeX code
local function build_table_begin(colspec, vertical_padding)
  return [[
\begin{table}[h]
\centering
{\renewcommand{\arraystretch}{]] .. vertical_padding .. [[}
\begin{tabular}{]] .. colspec .. [[}
\hline
]]
end

-- Build table end LaTeX code
local function build_table_end()
  return [[
\end{tabular}}
]]
end

-- Build caption LaTeX code
local function build_caption(caption)
  if caption and caption.long and #caption.long > 0 then
    local caption_text = inlines_to_latex(caption.long)
    return '\\caption{' .. caption_text .. '}\n'
  end
  return ''
end

-- Main filter function
function Table(tbl)
  if not FORMAT:match 'latex' then
    return tbl
  end
  
  local colspec = build_column_spec(tbl.colspecs)
  local blocks = pandoc.Blocks{}
  
  -- Table opening
  blocks:insert(pandoc.RawBlock('latex', build_table_begin(colspec, VERTICAL_PADDING)))
  
  -- Process header rows
  if tbl.head and tbl.head.rows and #tbl.head.rows > 0 then
    for _, row in ipairs(tbl.head.rows) do
      blocks:insert(format_header_row(row))
    end
  end
  
  -- Process body rows
  for _, body in ipairs(tbl.bodies) do
    for _, row in ipairs(body.body) do
      blocks:insert(format_body_row(row))
      blocks:insert(pandoc.RawBlock('latex', '\\hline'))
    end
  end
  
  -- Table end (tabular only)
  blocks:insert(pandoc.RawBlock('latex', build_table_end()))
  
  -- Caption (if exists)
  local caption_latex = build_caption(tbl.caption)
  if caption_latex ~= '' then
    blocks:insert(pandoc.RawBlock('latex', caption_latex))
  end
  
  -- Close table environment
  blocks:insert(pandoc.RawBlock('latex', '\\end{table}\n'))
  
  return blocks
end