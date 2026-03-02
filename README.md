# Recipe & Ingredient Manager

A Java-based desktop application designed to manage a database of food ingredients and recipes. It calculates total production costs, recommended selling prices, and profit margins while handling overheads and tax configurations.

## Core Features

* **Ingredient Management**: Add, edit, or delete ingredients with details such as name, supplier, cost per unit, and weight.
* **Recipe Composition**: Build recipes by adding specific amounts of ingredients from your database.
* **Automated Costing**:
    * **Cost to Make**: Calculates base costs including raw materials and overheads like manpower, electricity, and packaging.
    * **Selling Point**: Automatically generates suggested selling prices based on user-defined markup percentages and VAT selection.
* **VAT Editor**: A centralized tool to configure up to four different tax rates used throughout the application.
* **Financial Analytics**: Track key performance indicators including profit margin, food cost percentage, and net profit.
* **Data Persistence**: Automatically saves and loads all data from local text files (`recipes.txt`, `ingredients.txt`, `vatFile.txt`, and `idfile.txt`).

## Key Components

### Primary Dashboard
The main interface (`MyFrame`) serves as the central hub for the application. From here, users can access:
* **Recipe Management**: View and sort the complete list of recipes.
* **Ingredient Editor**: Open a dedicated window to manage raw material data.
* **VAT Editor**: Access and modify global tax settings that affect all ingredient and recipe calculations.

### VAT Editor
Accessible from the primary dashboard, the VAT Editor allows for the configuration of four distinct tax rates.
* **Rate Configuration**: Users can input tax values as decimals (e.g., `0.23` for 23%).
* **Global Impact**: Changing a rate in the VAT Editor automatically triggers a cost update for every ingredient and recipe that uses that specific tax selection.
* **Safe Range**: The system enforces a valid range between 0 and 1 (0% to 100%) for all tax inputs.

### Ingredient Editor
A dedicated window (`IngredientFrame`) for managing the inventory database.
* **Search & Filter**: Quickly find ingredients by name or supplier.
* **Tax Assignment**: Assign one of the four pre-configured VAT rates to each ingredient.

## Requirements
* **Java Runtime**: Built using standard Java libraries (`javax.swing` and `java.awt`).
* **File System**: The application requires read/write access to its root directory to maintain data files.

## How to Use
1. **Launch**: Run the `Main` class to open the primary dashboard.
2. **Configure Taxes**: Open the **VAT Editor** from the dashboard to set your local tax rates.
3. **Add Ingredients**: Use the **Ingredient Editor** to populate your database, ensuring you select the correct VAT rate for each item.
4. **Create Recipes**: Use the **Recipe Frame** to build recipes, adjust overhead costs (manpower, etc.), and set your desired markup percentage.