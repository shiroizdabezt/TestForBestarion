## 📁 Directory Structure

```
Markdown/
├── Bestarion.QM.CON.001-Controlled_Document_Convention-2_1/
├── Markdown/
├── PDF/
├── Template/
├── Tuan9/
├── README.md
└── UI.png
```

## 🚀 Quick Start

### Requirements
- **Pandoc**: Version 3.8.3
- **LaTeX Engine**: `xelatex`
- **Dependencies**: `librsvg2-bin`, `git`, `sed`

### Quick Deployment

#### 💻 Terminal (Manual Run)

To run the conversion locally, you can use the following commands:

```bash
# 1. Define variables
FILENAME="your_file.md"
OUTPUT_PATH="./Markdown/PDF/"
TEMPLATE_PATH="./Markdown/Template/template.tex"
LUA_FILTER_PATH="./Markdown/Template/convention.lua"
ESC=$(printf '\033')

# 2. Clean the markdown file (remove ANSI colors if any)
LC_ALL=C sed "s/${ESC}//g; s/${ESC}\[[0-9;]*[a-zA-Z]//g" "${FILENAME}" > "${FILENAME}.clean"

# 3. Run Pandoc
pandoc "${FILENAME}.clean" \
    -f markdown \
    -o "${OUTPUT_PATH}${FILENAME%.md}.pdf" \
    --template="${TEMPLATE_PATH}" \
    --pdf-engine=xelatex \
    --resource-path=. \
    --lua-filter="${LUA_FILTER_PATH}"

# 4. Cleanup
rm "${FILENAME}.clean"
```

#### 🤖 Jenkins (UI)

1. Go to the Jenkins UI.
2. Navigate to the `pdf_convert` job.
3. Click **Build with Parameters**.
4. Configure the options:

| Parameter             | Default        | Description                                                                                                  |
| --------------------- | -------------- | ------------------------------------------------------------------------------------------------------------ |
| `MODE`                | `CHANGED_ONLY` | `CHANGED_ONLY`: Converts only modified `.md` files.<br>`ALL`: Converts all `.md` files in the target folder. |
| `TARGET_FOLDER`       | `Markdown`     | The directory containing Markdown files to convert.                                                          |
| `FORCE_REBUILD_IMAGE` | `false`        | Set to `true` to force a rebuild of the Docker image.                                                        |

![alt text](UI.png)

> [!IMPORTANT]
> When running this pipeline (especially for automatic triggers), you **MUST** start your commit message with `doc: ` or `doc(<type>): ` (e.g., `doc: update readme` or `doc(markdown): fix typo`). Otherwise, the pipeline may skip the conversion.


## 📄 Automated PDF Conversion

The project includes a dedicated Jenkins pipeline (`pdf_convert`) to automate the conversion of Markdown documentation into PDF format.


### 📦 Output
- Generated PDFs are saved to: `Markdown/PDF/`
- Changes are automatically committed and pushed to the `main` branch with message `chore: auto-generated PDFs (<MODE>)`.


## Git Conventions

### 📝 Commit Message Format

We use [Conventional Commits](https://www.conventionalcommits.org/) to ensure consistent commit messages and automated changelog generation.

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

#### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Formatting changes, missing semicolons, etc. (no code logic change)
- **refactor**: Code changes that neither fix a bug nor add a feature
- **perf**: Performance improvements
- **test**: Adding or correcting tests
- **chore**: Changes to the build process, auxiliary tools, or libraries
- **ci**: Changes to CI configuration files and scripts
- **build**: Changes that affect the build system or external dependencies

