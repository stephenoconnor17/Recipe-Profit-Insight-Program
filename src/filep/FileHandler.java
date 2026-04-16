package filep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles reading and writing of all persistent data files: VAT rates,
 * ingredient ID counter, ingredients, and recipes. All files use a pipe (|)
 * delimiter.
 */
public class FileHandler {

	// ========================
	// File Names & Delimiters
	// ========================

	private String recipeFileName = "recipes.txt";
	private String ingredientFileName = "ingredients.txt";
	private String idFileName = "idfile.txt";
	private String vatFileName = "vatFile.txt";
	private final String delimiter = "|";
	private final String splitDelimiter = "\\|";

	/** Escapes pipe and newline characters so note text is safe for the pipe-delimited format. */
	private String escapeNote(String note) {
		if (note == null) return "";
		return note.replace("&", "&amp;").replace("|", "&pipe;").replace("\r", "").replace("\n", "&nl;");
	}

	/** Reverses escapeNote to restore original text. */
	private String unescapeNote(String note) {
		if (note == null) return "";
		return note.replace("&nl;", "\n").replace("&pipe;", "|").replace("&amp;", "&");
	}

	// ========================
	// VAT File
	// ========================

	/**
	 * Loads 4 VAT rates from file into VATHandler.vats. Values must be between 0
	 * and 1.
	 */
	public void loadVATFile() {
		File myFile = new File(vatFileName);
		try (Scanner scanner = new Scanner(myFile)) {
			int i = 0;
			while (scanner.hasNextLine() && i < 4) {
				String line = scanner.nextLine();
				try {
					Float nextVat = Float.valueOf(line);
					if (nextVat >= 0 && nextVat <= 1) {
						VATHandler.vats[i] = nextVat;
					} else {
						VATHandler.vats[i] = 0f;
					}

				} catch (Exception e) {

				}

				i++;
			}

		} catch (FileNotFoundException e) {

		}
	}

	/** Writes the current 4 VAT rates from VATHandler.vats to file. */
	public void writeVATFile() throws IOException {
		try (FileWriter myFile = new FileWriter(vatFileName)) {
			for (int i = 0; i < 4; i++) {
				if (VATHandler.vats[i] >= 0 && VATHandler.vats[i] <= 1) {
					myFile.write(String.valueOf(VATHandler.vats[i]) + "\n");
				} else {
					myFile.write("0\n");
				}
			}
		}
	}

	// ========================
	// ID File
	// ========================

	/** Loads the next available ingredient ID from file into RecipeHandler. */
	public void loadIdFile() {
		File myFile = new File(idFileName);
		try (Scanner scanner = new Scanner(myFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				try {
					Integer nextID = Integer.valueOf(line);
					if (nextID != null && nextID > 0) {
						RecipeHandler.nextAvailableID = nextID;
					} else {
						RecipeHandler.nextAvailableID = RecipeHandler.ingredients.size() + 1;
					}
				} catch (Exception e) {

				}
			}

		} catch (FileNotFoundException e) {

		}
	}

	/** Writes the current next-available ID to file. */
	public void writeIdFile() throws IOException {
		try (FileWriter myFile = new FileWriter(idFileName)) {
			myFile.write(String.valueOf(RecipeHandler.nextAvailableID));
		}
	}

	// ========================
	// Ingredients File
	// ========================

	/**
	 * Loads ingredients from file. Each line format:
	 * name|supplier|cost|grams|id|vatSelection
	 */
	public void loadIngredients() {
		File myFile = new File(ingredientFileName);
		try (Scanner scanner = new Scanner(myFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				try {
					String[] parts = line.split(splitDelimiter);
					if (parts.length < 6)
						continue;

					String name = parts[0];
					String supplier = parts[1];
					float cost = Float.parseFloat(parts[2]);
					float grams = Float.parseFloat(parts[3]);
					int id = Integer.parseInt(parts[4]);
					int vatSelection = Integer.parseInt(parts[5]);
					String unitType = (parts.length >= 7) ? parts[6] : "grams";

					Ingredient temp = new Ingredient(name, supplier, cost, grams, id, vatSelection, unitType);
					RecipeHandler.ingredients.add(temp);
					RecipeHandler.ingredientIDMap.put(id, temp);
					RecipeHandler.ingredientByName.put(temp.getName(), temp);
				} catch (Exception e) {
					System.err.println("Skipping malformed ingredient line: " + line);
				}
			}

		} catch (FileNotFoundException e) {

		}
	}

	/** Writes all ingredients to file and persists the current ID counter. */
	public void writeIngredients() throws IOException {
		try (FileWriter myfw = new FileWriter(ingredientFileName)) {
			for (int i = 0; i < RecipeHandler.ingredients.size(); i++) {
				Ingredient current = RecipeHandler.ingredients.get(i);

				myfw.write(current.getName() + delimiter + current.getSupplierName() + delimiter
						+ (current.getCostPer1g() * current.getGrams()) + delimiter + current.getGrams() + delimiter
						+ current.getID() + delimiter + current.getVatSelection() + delimiter
						+ current.getUnitType() + "\n");
			}
		}

		writeIdFile();
	}

	// ========================
	// Recipes File
	// ========================

	/**
	 * Loads recipes from file. Supports two formats:
	 * v1 (legacy): name|markup|vatSelection|packaging|manpower|electricity|[allergens|personalNote]|ingredients...
	 * v2 (current): name|markup|vatSelection|v2|elecRate|elecMins|manRate|manMins|pkgPerUnit|units|allergens|personalNote|ingredients...
	 * Ingredient references are resolved by ID from RecipeHandler.ingredientIDMap.
	 */
	public void loadRecipes() {
		File myFile = new File(recipeFileName);
		try (Scanner scanner = new Scanner(myFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				try {
					String[] parts = line.split(splitDelimiter, -1);
					if (parts.length < 6)
						continue;

					String name = parts[0];
					float markUp = Float.parseFloat(parts[1]);
					int vatSelection = Integer.parseInt(parts[2]);
					Recipe temp = new Recipe(name);

					int ingredientStart;

					if (parts.length >= 12 && parts[3].equals("v2")) {
						// v2 format: rate-based utilities
						temp.setElectricityRate(Float.parseFloat(parts[4]));
						temp.setElectricityMinutes(Float.parseFloat(parts[5]));
						temp.setManpowerRate(Float.parseFloat(parts[6]));
						temp.setManpowerMinutes(Float.parseFloat(parts[7]));
						temp.setPackagingCostPerUnit(Float.parseFloat(parts[8]));
						temp.setUnitsPerRecipe(Integer.parseInt(parts[9]));
						temp.setAllergens(unescapeNote(parts[10]));
						temp.setPersonalNote(unescapeNote(parts[11]));
						ingredientStart = 12;
					} else {
						// v1 legacy format: flat costs
						float packaging = Float.parseFloat(parts[3]);
						float manpower = Float.parseFloat(parts[4]);
						float electricity = Float.parseFloat(parts[5]);

						// Migrate flat costs: treat as rate with 1 minute, 1 unit
						temp.setElectricityRate(electricity);
						temp.setElectricityMinutes(1);
						temp.setManpowerRate(manpower);
						temp.setManpowerMinutes(1);
						temp.setPackagingCostPerUnit(packaging);
						temp.setUnitsPerRecipe(1);

						// Detect notes in v1 format
						ingredientStart = 6;
						if (parts.length >= 8 && !parts[6].contains("/")) {
							temp.setAllergens(unescapeNote(parts[6]));
							temp.setPersonalNote(unescapeNote(parts[7]));
							ingredientStart = 8;
						}
					}

					// Each remaining part is an ingredient reference: id/gramsUsed
					for (int i = ingredientStart; i < parts.length; i++) {
						String[] ingredientParts = parts[i].split("/");
						if (ingredientParts.length < 2)
							continue;

						int ingredientId = Integer.parseInt(ingredientParts[0]);
						float grams = Float.parseFloat(ingredientParts[1]);

						Ingredient ing = RecipeHandler.ingredientIDMap.get(ingredientId);

						if (ing != null) {
							temp.addIngredient(ing, grams);
						}
					}

					temp.setMarkUp(markUp);
					temp.setVatSelection(vatSelection);

					RecipeHandler.recipes.add(temp);
					RecipeHandler.recipeByName.put(temp.getName(), temp);
				} catch (Exception e) {
					System.err.println("Skipping malformed recipe line: " + line);
				}
			}

		} catch (FileNotFoundException e) {

		}
	}

	/** Writes all recipes to file, including their ingredient references. */
	public void writeRecipes() throws IOException {
		try (FileWriter myfw = new FileWriter(recipeFileName)) {
			for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
				Recipe current = RecipeHandler.recipes.get(i);

				myfw.write(current.getName() + delimiter + current.getMarkUp() + delimiter + current.getVatSelection()
						+ delimiter + "v2"
						+ delimiter + current.getElectricityRate() + delimiter + current.getElectricityMinutes()
						+ delimiter + current.getManpowerRate() + delimiter + current.getManpowerMinutes()
						+ delimiter + current.getPackagingCostPerUnit() + delimiter + current.getUnitsPerRecipe()
						+ delimiter + escapeNote(current.getAllergens())
						+ delimiter + escapeNote(current.getPersonalNote()));
				for (int j = 0; j < current.returnIngredientListSize(); j++) {
					Ingredient myI = current.getIngredientFromList(j);
					myfw.write(delimiter + myI.getID() + "/" + current.getGramsUsedOfIngredient(myI));
				}
				myfw.write("\n");
			}
		}
	}
}
