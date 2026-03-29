# RecipeProfitizer

A Java Swing desktop application for managing food ingredients and recipes. It calculates production costs, recommended selling prices, and profit margins while handling overheads and VAT.

## Features

- **Ingredient Management** — Add, edit, and delete ingredients with supplier info, unit cost, and amount bought. Choose a unit type (grams, ml, or units) and assign one of four configurable VAT rates to each ingredient.
- **Recipe Builder** — Compose recipes from your ingredient database. Set rate-based overhead costs (electricity rate/minutes, manpower rate/minutes, packaging cost per unit) and markup percentages. Track allergens and personal notes per recipe.
- **Automated Costing** — Calculates cost-to-make (including overheads and VAT), suggested selling price (based on markup and VAT), and net profit per recipe. Displays per-unit cost and selling point when producing multiple units from one recipe batch.
- **Financial Analytics** — View profit margin, food cost percentage, food cost difference, and net profit at a glance in a colour-coded dashboard table.
- **Sorting** — Sort recipes and ingredients by name, cost, selling price, profit, margin, or ingredient count (ascending/descending).
- **VAT Editor** — Configure up to four global tax rates. Changing a rate automatically recalculates every ingredient and recipe that uses it.
- **Search & Filter** — Quickly find recipes by name, and ingredients by name or supplier, with real-time filtering.
- **Double-Click Navigation** — Double-click any recipe row in the dashboard to jump straight into the recipe editor.
- **Data Persistence** — All data is saved and loaded from local text files (`recipes.txt`, `ingredients.txt`, `vatFile.txt`, `idfile.txt`). Supports automatic migration from legacy (v1) to current (v2) recipe format.
- **Single Instance Protection** — Prevents multiple instances from running simultaneously to avoid data corruption.

## Screenshots

![Main Dashboard](screenshots/main-dashboard.png)

![Recipe Editor](screenshots/recipe-editor.png)

![Ingredient Editor](screenshots/ingredient-editor.png)

![VAT Editor](screenshots/vat-editor.png)

![Add Ingredient Dialog](screenshots/add-ingredient-dialog.png)

## Technologies Used

- **Java** (8+) — Core language
- **Swing / AWT** — GUI framework (`javax.swing`, `java.awt`)
- No external libraries or dependencies

## Getting Started

### Prerequisites

- **Java 8+** (built with standard `javax.swing` and `java.awt` libraries — no external dependencies)
- Read/write access to the application's root directory for data files

### Running the Application

```bash
# From the project root, compile all sources:
javac -d bin src/mainp/Main.java src/filep/*.java src/myGUI/*.java src/util/*.java

# Run the application:
java -cp bin mainp.Main
```

Alternatively, open the project in any Java IDE (Eclipse, IntelliJ, etc.) and run `mainp.Main`.

### Usage

1. **Configure VAT** — Open the VAT Editor from the dashboard to set your local tax rates.
2. **Add Ingredients** — Use the Ingredient Editor to populate your database, selecting the correct VAT rate and unit type for each item.
3. **Create Recipes** — Use the Recipe Editor to build recipes, set overhead costs (electricity, manpower, packaging), choose your markup percentage, and record allergen/note information.
4. **Review Profitability** — The main dashboard table shows cost, selling price, profit margin, and food cost metrics for every recipe at a glance. Double-click any row to edit.

## Project Structure

```
src/
├── mainp/
│   └── Main.java                 # Entry point
├── filep/
│   ├── FileHandler.java          # Read/write data files
│   ├── RecipeHandler.java        # In-memory recipe & ingredient store
│   ├── Ingredient.java           # Ingredient model
│   ├── Recipe.java               # Recipe model
│   ├── VATHandler.java           # VAT rate management
│   ├── IngredientSortType.java   # Ingredient sort options enum
│   └── RecipeSortType.java       # Recipe sort options enum
├── myGUI/
│   ├── MyFrame.java              # Primary dashboard window
│   ├── MyPanel.java              # Main panel with recipe table
│   ├── RecipeFrame.java          # Recipe editor
│   ├── IngredientFrame.java      # Ingredient editor
│   ├── AddIngredientDialog.java  # Dialog for adding ingredients to a recipe
│   └── VATFrame.java             # VAT rate editor
└── util/
    ├── CellRenderer.java         # Colour-coded table cell renderer
    ├── ComboBoxUtil.java         # Dropdown helper methods
    ├── MinSizeEnforcer.java      # Minimum window size enforcer
    ├── SortUtil.java             # Sorting utilities
    └── TextChangeListener.java   # Live text-change listener
```

## AI Acknowledgement

From commit [`c4e8e0b`](../../commit/c4e8e0b) onwards, **Claude** (by Anthropic) has assisted in the development of this project. Specifically, AI was used for:

- **Layout refactor** — Migrating from absolute positioning to `BorderLayout` and `GridBagLayout` across all GUI frames
- **VAT integration** — Integrating VAT handling, calculation, and UI throughout the application
- **Feature implementation** — Rate-based overhead costs, per-unit pricing, unit type support, allergen/note fields, double-click navigation, recipe format v2 migration, and sorting options
- **Boilerplate refactoring** — Consolidating repetitive code into shared utility methods (`CellRenderer`, `ComboBoxUtil`, `SortUtil`, `TextChangeListener`)
- **Code review** — Identifying bugs, improving error handling, and replacing silent exceptions with user-visible error dialogs
- **README documentation** — Structuring and updating this README to accurately reflect all features

All AI-assisted changes were reviewed, tested, and directed by the project author. The author understands all code in this repository.

## License

This project is not currently under a formal licence.
