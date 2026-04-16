# RecipeProfitizer

A Java Swing desktop application for managing food ingredients and recipes. It calculates production costs, recommended selling prices, and profit margins while handling overheads and VAT.

## Features

- **Recipe Dashboard** — Central window listing every recipe with a colour-coded financial table (Overall Cost, Markup, VAT Type, VAT Paid, Sale, Difference, Profit Margin, Food Cost Diff). Double-click any row to open it in the recipe editor.
- **Per-Unit Pricing** — Recipes that produce multiple units (e.g. a tray of cookies) expose cost-per-unit and selling-point-per-unit alongside the batch totals, so a recipe yielding 12 cookies shows what each cookie costs to make and what each should sell for.
- **Recipe Editor** — Build recipes from your saved ingredients. Configure rate-based overheads (electricity rate × minutes, manpower rate × minutes), per-unit packaging cost, and units-per-recipe yield. Set markup percentage, assign one of four VAT rates, preview the post-markup-and-VAT price, and record allergens and personal notes.
- **Ingredient Editor** — Add, edit, and delete ingredients with supplier, unit cost, amount bought, unit type (grams, ml, or units), and a per-ingredient VAT selection. Recipes that use an ingredient are recalculated automatically when the ingredient changes (or is deleted).
- **VAT Editor** — Configure up to four global VAT rates. Editing a rate automatically recalculates every ingredient and recipe that uses it.
- **Sorting** — Sort recipes by cost, selling point, name, profit, food margin, or ingredient count (ascending/descending). Sort ingredients by cost, name, or ID.
- **Search & Filter** — Real-time keyword search for recipes (by name) and ingredients (by name or supplier).
- **Input Validation** — Numeric fields enforce their unit type — 'units' fields require whole numbers, weight/volume fields accept decimals — and surface clear popup errors instead of silently coercing bad input.
- **Data Persistence** — All data is saved to and loaded from local text files (`recipes.txt`, `ingredients.txt`, `vatFile.txt`, `idfile.txt`), with automatic migration from the legacy v1 to current v2 recipe format.
- **Single Instance Protection** — Prevents multiple application instances from running simultaneously to avoid data corruption.

## Screenshots

### Main Dashboard
The primary window, showing every recipe alongside its cost, selling price, profit margin, and food cost percentage in a colour-coded table. Double-click a row to jump into the recipe editor.

![Main Dashboard](screenshots/main-dashboard.png)

### Recipe Editor
Build and edit recipes by composing them from saved ingredients. Set markup, electricity and manpower rates/minutes, packaging cost, units per recipe, and record allergens and personal notes.

![Recipe Editor](screenshots/recipe-editor.png)

### Ingredient Editor
Manage your ingredient database with supplier, cost, amount bought, unit type (grams, ml, or units), and per-ingredient VAT rate.

![Ingredient Editor](screenshots/ingredient-editor.png)

### VAT Editor
Configure up to four global VAT rates. Editing a rate automatically recalculates every ingredient and recipe that uses it.

![VAT Editor](screenshots/vat-editor.png)

### Add Ingredient Dialog
Search the ingredient database and add a chosen ingredient to the current recipe, with input validation that adapts to the ingredient's unit type.

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
2. **Add Ingredients** — Use the Ingredient Editor to populate your database, selecting the correct VAT rate and unit type (grams, ml, or units) for each item.
3. **Create Recipes** — Use the Recipe Editor to build recipes from your ingredients, set overheads (electricity rate/minutes, manpower rate/minutes, packaging cost per unit), specify the recipe yield (units per recipe), choose markup and VAT, and record allergens and personal notes.
4. **Review Profitability** — The dashboard table shows cost, selling price, profit margin, and food cost metrics for every recipe. Double-click any row to open it in the editor, where both the recipe-batch totals and the per-unit cost and selling point are displayed.

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
