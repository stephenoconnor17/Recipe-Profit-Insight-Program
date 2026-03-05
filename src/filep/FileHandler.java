package filep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles reading and writing of all persistent data files:
 * VAT rates, ingredient ID counter, ingredients, and recipes.
 * All files use a pipe (|) delimiter.
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

	// ========================
	// VAT File
	// ========================

	/** Loads 4 VAT rates from file into VATHandler.vats. Values must be between 0 and 1. */
	public void loadVATFile() {
		File myFile = new File(vatFileName);
		try (Scanner scanner = new Scanner(myFile)) {
			int i = 0;
			while (scanner.hasNextLine() && i < 4) {
				String line = scanner.nextLine();
				try {
					Float nextVat = Float.valueOf(line);
					if(nextVat >= 0 && nextVat <= 1) {
						VATHandler.vats[i] = nextVat;
					}else {
						VATHandler.vats[i] = 0f;
					}

				}catch(Exception e) {

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
					if(nextID != null && nextID > 0) {
						RecipeHandler.nextAvailableID = nextID;
					}else {
						RecipeHandler.nextAvailableID = RecipeHandler.ingredients.size() + 1;
					}
				}catch(Exception e) {

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
				String[] parts = line.split(splitDelimiter);

				String name = parts[0];
				String supplier = parts[1];
				float cost = Float.parseFloat(parts[2]);
				float grams = Float.parseFloat(parts[3]);
				int id = Integer.parseInt(parts[4]);
				int vatSelection = Integer.parseInt(parts[5]);

				Ingredient temp = new Ingredient(name, supplier, cost, grams, id, vatSelection);
				RecipeHandler.ingredients.add(temp);
				RecipeHandler.ingredientIDMap.put(id, temp);
				RecipeHandler.ingredientByName.put(temp.getName(), temp);
			}

		} catch (FileNotFoundException e) {

		}
	}

	/** Writes all ingredients to file and increments the ID counter. */
	public void writeIngredients() throws IOException {
		try (FileWriter myfw = new FileWriter(ingredientFileName)) {
			for (int i = 0; i < RecipeHandler.ingredients.size(); i++) {
				Ingredient current = RecipeHandler.ingredients.get(i);

				myfw.write(current.getName() + delimiter + current.getSupplierName() + delimiter
						+ (current.getCostPer1g() * current.getGrams()) + delimiter + current.getGrams() + delimiter + current.getID()
						+ delimiter + current.getVatSelection() + "\n");
			}
		}

		RecipeHandler.nextAvailableID++;
		writeIdFile();
	}

	// ========================
	// Recipes File
	// ========================

	/**
	 * Loads recipes from file. Each line format:
	 * name|markup|vatSelection|packaging|manpower|electricity|ingredientId/grams|...
	 * Ingredient references are resolved by ID from RecipeHandler.ingredientIDMap.
	 */
	public void loadRecipes() {
		File myFile = new File(recipeFileName);
		try (Scanner scanner = new Scanner(myFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split(splitDelimiter);

				String name = parts[0];
				float markUp = Float.parseFloat(parts[1]);
				int vatSelection = Integer.parseInt(parts[2]);
				float packaging = Float.parseFloat(parts[3]);
				float manpower = Float.parseFloat(parts[4]);
				float electricity = Float.parseFloat(parts[5]);
				Recipe temp = new Recipe(name);

				// Each remaining part is an ingredient reference: id/gramsUsed
				for (int i = 6; i < parts.length; i++) {
					String[] ingredientParts = parts[i].split("/");

					int ingredientId = Integer.parseInt(ingredientParts[0]);
					float grams = Float.parseFloat(ingredientParts[1]);

					Ingredient ing = RecipeHandler.ingredientIDMap.get(ingredientId);

					if (ing != null) {
						temp.addIngredient(ing, grams);
					}
				}

				temp.setMarkUp(markUp);
				temp.setVatSelection(vatSelection);
				temp.setElectricityCost(electricity);
				temp.setManPowerCost(manpower);
				temp.setPackagingCost(packaging);

				RecipeHandler.recipes.add(temp);
				RecipeHandler.recipeByName.put(temp.getName(), temp);
			}

		} catch (FileNotFoundException e) {

		}
	}

	/** Writes all recipes to file, including their ingredient references. */
	public void writeRecipes() throws IOException {
		try (FileWriter myfw = new FileWriter(recipeFileName)) {
			for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
				Recipe current = RecipeHandler.recipes.get(i);

				myfw.write(current.getName() + delimiter + current.getMarkUp() + delimiter + current.getVatSelection() + delimiter + current.getPackagingCost() + delimiter + current.getManPowerCost() + delimiter + current.getElectricityCost());
				for (int j = 0; j < current.returnIngredientListSize(); j++) {
					Ingredient myI = current.getIngredientFromList(j);
					myfw.write(delimiter + myI.getID() + "/" + current.getGramsUsedOfIngredient(myI));
				}
				myfw.write("\n");
			}
		}
	}
}
