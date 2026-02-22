package filep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {
	private String recipeFileName = "recipes.txt";
	private String ingredientFileName = "ingredients.txt";
	private final String delimiter = "|";
	private final String splitDelimiter = "\\|";

	// ingredients in the form of name suppliername cost grams id
	// read each ingredient
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

				Ingredient temp = new Ingredient(name, supplier, cost, grams, id);
				RecipeHandler.ingredients.add(temp);
				RecipeHandler.ingredientIDMap.put(id, temp);
				RecipeHandler.ingredientByName.put(temp.getName(), temp);
			}

		} catch (FileNotFoundException e) {

		}
	}

	// write ingredient list back down to file
	public void writeIngredients() throws IOException {
		FileWriter myfw = new FileWriter(ingredientFileName);
		for (int i = 0; i < RecipeHandler.ingredients.size(); i++) {
			Ingredient current = RecipeHandler.ingredients.get(i);

			myfw.write(current.getName() + delimiter + current.getSupplierName() + delimiter
					+ (current.getCostPer1g() * current.getGrams()) + delimiter + current.getGrams() + delimiter + current.getID()
					+ "\n");
		}
		myfw.close();
	}

	// load recipes from file.
	// they should be in the format of name, sellpoint,packaging,manpower,electricity, ids
	public void loadRecipes() {
		File myFile = new File(recipeFileName);
		try (Scanner scanner = new Scanner(myFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split(splitDelimiter);

				String name = parts[0];
				float cost = Float.parseFloat(parts[1]);
				float packaging = Float.parseFloat(parts[2]);
				float manpower = Float.parseFloat(parts[3]);
				float electricity = Float.parseFloat(parts[4]);
				Recipe temp = new Recipe(name, cost);

				for (int i = 5; i < parts.length; i++) {
					String[] ingredientParts = parts[i].split("/");

					int ingredientId = Integer.parseInt(ingredientParts[0]);
					float grams = Float.parseFloat(ingredientParts[1]);

					Ingredient ing = RecipeHandler.ingredientIDMap.get(ingredientId);

					if (ing != null) {
						temp.addIngredient(ing, grams);
					}
				}
				
				temp.setElectricityCost(electricity);
				temp.setManPowerCost(manpower);
				temp.setPackagingCost(packaging);

				RecipeHandler.recipes.add(temp);
				RecipeHandler.recipeByName.put(temp.getName(), temp);
			}

		} catch (FileNotFoundException e) {

		}
	}

	public void writeRecipes() throws IOException {
		FileWriter myfw = new FileWriter(recipeFileName);
		for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
			Recipe current = RecipeHandler.recipes.get(i);

			myfw.write(current.getName() + delimiter + current.getSellPoint() + delimiter + current.getPackagingCost() + delimiter + current.getManPowerCost() + delimiter + current.getElectricityCost());
			for (int j = 0; j < current.returnIngredientListSize(); j++) {
				Ingredient myI = current.getIngredientFromList(j);
				myfw.write(delimiter + myI.getID() + "/" + current.getGramsUsedOfIngredient(myI));
			}
			myfw.write("\n");

		}
		myfw.close();
	}
}
