# Markdown Convert

## Introduction

This file describes the rules regarding syntax, variable naming, and procedures for converting **Markdown** documents to **.pdf** files according to the document standardization standards of **BESTARION**.

The primary goal of this process is to accelerate the document writing speed of team members, allowing the documentation of important processes in raw **.md** format, and then converting them to complete **.pdf** files without spending time on document standardization.

## Rules

This section describes the specific rules, standards, and steps in the process of converting **.md** files to **.pdf**.

Currently, the following standards will be guaranteed when performing the conversion:

### 1. Page Formatting

- **Body Text**

  - Align: Justified.
  - Font: Arial, 10pt, Black.

- **Margin**
  
  - Right, Left, Top, Bottom: 0.59 inch
  - Header, Footer: 0.30 inch
  
- **Layout**: A4 Paper.

### 2. Table Formatting

The project supports rendering table structures in **.md** files to the correct formatting standards defined.

Currently, the project only supports rendering simple table structures as follows:

```md
| Table header 1 | Table header 2 | Table header 3 |
|:---------------|:--------------:|---------------:| 
| Row 1, Col 1   |     Column 2   |    Column 3    |
| **Row 1 Col 2**  | *Row 1 Col 2*  | **_Row 1 Col 3_** |

: This is caption for above table
```

The rendered table is applied with the following styles:

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

- Table Caption:

  - **Font**: Arial italic.
  - **Size**: 9pt.
  - **Align**: Center
  - **Color**: Black.

- Table Align: Center

![example table](../images/image-2.png)

### 3. Image Formatting

How to add an image to a markdown file:
```md
![this is image caption](./image.png)
```
The content inside `![]` is the image caption. The content inside `()` is the path to the image file.  

Rendered images will have the following styles applied:
- Image Caption:

  - **Font**: Arial italic.
  - **Size**: 9pt.
  - **Align**: Center
  - **Color**: Black.

- Image Align: Center

![example image](../images/image-3.png)  

### 4. Text Formatting

| Markdown Syntax | Style Convention | Description |
| :-- | :-- | :-- |
| `#` | Heading 1 | **Font**: Arial, Bold<br>**Size**: 12<br>**Align**: Left<br>**Color**: Black<br>**Background color**: (RGB: 245, 130, 32) |
| `##` | Heading 2 | **Font**: Arial, Bold<br>**Size**: 11<br>**Align**: Left<br>**Color**: Black |
| `###` | Heading 3 | **Font**: Arial, Bold<br>**Size**: 10<br>**Align**: Left<br>**Color**: Black |
| `- <List Item>` | List 1 | **Font**: Arial, Regular<br>**Size**: 10<br>**Align**: Left<br>**Character**: `●` |
| `__ - <List Item 2>` | List 2 | **Font**: Arial, Regular<br>**Size**: 10<br>**Align**: Left<br>**Character**: `■` |
| `____ - <List Item 3>` | List 3 | **Font**: Arial, Regular<br>**Size**: 10<br>**Align**: Justified<br>**Character**: `•` |

![example list](../images/image-4.png)

### 5. Metadata Variables

The **.md** file to be converted needs to have a special yaml field defined by two `---` marks at the beginning of the file. The purpose of this yaml block is to define **metadata variables** that will be used by the LaTeX engine to render the cover pages in the output **.pdf** file.

The cover pages will be generated based on metadata variables including:

- Cover page.
- Document control.

Below is an example of a yaml block:

```yaml
---
variable_1: "value 1"
variable_2: "value 2"
...
---

<!-- Specific content of the markdown file -->
```

The process of converting Markdown → PDF consists of 3 main steps as follows:

- Step 1: Pandoc reads the Markdown file and extracts YAML metadata.
- Step 2: Pandoc replaces variables in the LaTeX template with values from metadata.
- Step 3: LaTeX engine (xelatex) renders the template combined with the filter using a custom Lua script into the final PDF file.

Variables are divided into 2 main types as follows:

- **Pandoc Standard**: Variables predefined by Pandoc such as title, author, date, etc.
- **Custom Variables**: Variables defined by the document itself such as doc_code, updated_by, etc.

**1) Pandoc Standard Variables**

| Variables | Type | Description | Example |
| :-------- | :--- | :----------- | :------ |
| title | String | Document Name  | ORGANIZATIONAL PROCESS FOCUS PROCESS | 
| subtitle | String | Document Type Code | MSS |
| security | String | Security Classification | INTERNAL |
| version | String | Document Version | 1.0 |

**2) Custom Variables**

| Variables | Type | Description | Example |
| :-------- | :--- | :----------- | :------ |
| template_id | String | Template ID  | ODT\_Base\_Template | 
| header_line | String | Header content | Bestarion.QM.CON.001-Controlled\_Document\_Convention-2\_1 |
| security | String | Security Classification | INTERNAL |
| version | String | Document Version | 1.0 |
| updated_by | String | Person who made the last update | Tuan Nguyen-Quoc-Anh |
| effective_date | String | Date of the last update | Dec 08, 2025 | 
| changelog | Array[Object] | List of **change log** objects | |

**3) Object: Change Log**

| Variables | Type | Description | Example |
| :-------- | :--- | :----------- | :------ |
| version | String | Update version  | 1.0 | 
| description | String | Description about changes | Update content section 4. Document Convention |
| changed_by | String | Person who made the change | Tuan Nguyen-Quoc-Anh |
| changed_date | String | Date of change | 2025-02-10 |
| approved | String | Person who approved the change | N/A |
| approval_date | String | Date of approval | N/A | 

For the naming rules in **1)**, **2)** and **3)**, please follow the rules defined in the documents below:

- Bestarion.QM.CON.001-Controlled_Document_Convention-2_1.pdf

- Bestarion.QM.CON.004-Document_Responsibility_Authority_Standard-1_5.pdf

Below is an example of a complete yaml block:

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
    description: "Create new organizational process focus procedure."
    changed_by: "TuanNQA"
    changed_date: "2025-12-08"
    approved: "N/A"
    approval_date: "N/A"
  - version: "1.1"
    description: "Add section 4.2 about metrics and KPIs."
    changed_by: "AnhLT"
    changed_date: "2025-12-15"
    approved: "CTO/HungPV"
    approval_date: "2025-12-20"
  - version: "1.2"
    description: "Update template and format according to new standards."
    changed_by: "MaiNTH"
    changed_date: "2025-12-25"
    approved: "N/A"
    approval_date: "N/A"
---
```

The content of the two corresponding cover pages will be created as follows:

![Demo generate 2 pages](../images/image-1.png)

## Convention

This section describes some rules that need to be followed when writing **.md** files to convert them to **.pdf** with LaTeX without encountering errors.

### 1. Clear Separation Between Different Markdown Syntaxes

The components in the markdown file must be clearly separated by:

- Leaving a blank line between different elements.
- Or using two spaces when elements need to be adjacent.

This helps LaTeX correctly identify each element and apply the correct formatting.

**Incorrect Writing Example:**

```md
Content text above.
- List 1
  - List Item 1.1
  - List Item 1.2
      - List Item 1.2.1
- List 2
- List 3
Content text below.
```

*Problem:* LaTeX will misunderstand and merge text with list into one block, losing formatting.

**Correct Writing Example:**

```md
Content text above.

- List 1

  - List Item 1.1
  - List Item 1.2

      - List Item 1.2.1

- List 2
- List 3

Content text below.
```

*Result:* LaTeX will render 3 separate elements: text above → list → smaller list level → text below.

### 2. Do Not Use Duplicate Captions with Images

Tables do not have a default caption, so Pandoc supports adding a special syntax to create a caption which is `: caption`. 

In contrast, for images in markdown files, Pandoc will automatically extract the alt text content to use as the figure caption for the image.

LaTeX will rely on the occurrence of these elements + their captions to update the contents for the two corresponding pages which are **Index of Table** and **Index of Figures**.

Using both `: caption` combined with the alt text of the image will confuse LaTeX syntax and will not render the content of these two pages correctly.

**Incorrect Writing Example:**

```md

![this alt text should be used as image's figure caption, but it does not](image.png)

: this caption will be used as image's figure.
```

*Result:* LaTeX will still try to use the caption below as the figure caption for the image above, but the corresponding entry of this image in the **Index of figures** table will not be updated.

**Correct Writing Example:**

```md

![this alt text will be used as image's figure caption](image.png)
```

*Result:* LaTeX will render the image along with the caption defined in the alt text, as well as update the entry of the **Index of figures** table accordingly.
