package filep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import util.SortUtil;

public class RecipeHandler {
	public static ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public static Map<Integer, Ingredient> ingredientIDMap = new HashMap<>();
	public static Map<String, Ingredient> ingredientByName = new HashMap<>();

	public static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	public static Map<String, Recipe> recipeByName = new HashMap<>();
	public static int nextAvailableID = 0;

	public static void updateRecipes() {
		for (int i = 0; i < recipes.size(); i++) {
			recipes.get(i).update();
		}
	}

	public static void addIngredient(Ingredient i) {
		ingredients.add(i);
		ingredientIDMap.put(i.getID(), i);
		ingredientByName.put(i.getName(), i);
	}

	public static void addRecipe(Recipe r) {
		recipes.add(r);
		recipeByName.put(r.getName(), r);
	}

	public static boolean verifyNoIngredientCopy(Ingredient check) {
		boolean toReturn = true;

		for (Ingredient i : RecipeHandler.ingredients) {
			if (check.compareToIngredient(i)) {
				toReturn = false;
			}
		}

		return toReturn;
	}

	public static boolean verifyNoRecipeCopy(Recipe check) {
		boolean toReturn = true;

		for (Recipe i : RecipeHandler.recipes) {
			if (check.compareToRecipe(i)) {
				toReturn = false;
			}
		}

		return toReturn;
	}

	public static void sortRecipes(RecipeSortType sortType) {
        SortUtil.sortRecipes(recipes, sortType);
    }
	
	public static void sortIngredients(IngredientSortType sortType) {
       SortUtil.sortIngredients(ingredients, sortType);
    }
}
