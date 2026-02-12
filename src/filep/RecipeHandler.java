package filep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeHandler {
	public static ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public static Map<Integer, Ingredient> ingredientIDMap = new HashMap<>();
	public static Map<String, Ingredient> ingredientByName = new HashMap<>();

	public static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	public static Map<String, Recipe> recipeByName = new HashMap<>();

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
        switch (sortType) {
            // Default / fallback
            case DEFAULT:
            	//do nothing lol.
                break;

            // Cost-related
            case BY_COST_ASC:
                break;
            case BY_COST_DESC:
                break;

            // Name-related
            case BY_NAME_ASC:
                break;
            case BY_NAME_DESC:
                break;

            // Profit / margin
            case BY_PROFIT_ASC:
                break;
            case BY_PROFIT_DESC:
                break;
            case BY_MARGIN_ASC:
                break;
            case BY_MARGIN_DESC:
                break;

            // Ingredient count
            case BY_INGREDIENT_COUNT_ASC:
                break;
            case BY_INGREDIENT_COUNT_DESC:
                break;

            // Filtering-style views
            case SHOW_ALL:
                break;
            case SHOW_PROFITABLE:
                break;
            case SHOW_UNPROFITABLE:
                break;
        }
    }
	
	public static void sortIngredients(IngredientSortType sortType) {
        switch (sortType) {
            // Default / fallback
            case DEFAULT:
                break;

            // Cost-related
            case BY_COST_ASC:
                break;
            case BY_COST_DESC:
                break;

            // Name-related
            case BY_NAME_ASC:
                break;
            case BY_NAME_DESC:
                break;

            // ID-related
            case BY_ID_ASC:
                break;
            case BY_ID_DESC:
                break;
        }
    }
}
