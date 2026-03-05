package filep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import util.SortUtil;

/**
 * Central data store for all ingredients and recipes.
 * Provides static collections and methods for adding, verifying, sorting, and updating data.
 */
public class RecipeHandler {

	// ========================
	// Data Collections
	// ========================

	public static ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public static Map<Integer, Ingredient> ingredientIDMap = new HashMap<>();
	public static Map<String, Ingredient> ingredientByName = new HashMap<>();

	public static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	public static Map<String, Recipe> recipeByName = new HashMap<>();

	/** The next ID to assign when creating a new ingredient. */
	public static int nextAvailableID = 0;

	// ========================
	// Add Operations
	// ========================

	/** Registers a new ingredient in all lookup collections. */
	public static void addIngredient(Ingredient i) {
		ingredients.add(i);
		ingredientIDMap.put(i.getID(), i);
		ingredientByName.put(i.getName(), i);
	}

	/** Registers a new recipe in the list and name-lookup map. */
	public static void addRecipe(Recipe r) {
		recipes.add(r);
		recipeByName.put(r.getName(), r);
	}

	// ========================
	// Duplicate Verification
	// ========================

	/** Returns true if no existing ingredient matches the given one (by ID or name+supplier). */
	public static boolean verifyNoIngredientCopy(Ingredient check) {
		boolean toReturn = true;

		for (Ingredient i : RecipeHandler.ingredients) {
			if (check.compareToIngredient(i)) {
				toReturn = false;
			}
		}

		return toReturn;
	}

	/** Returns true if no existing recipe matches the given one (by name+sell point). */
	public static boolean verifyNoRecipeCopy(Recipe check) {
		boolean toReturn = true;

		for (Recipe i : RecipeHandler.recipes) {
			if (check.compareToRecipe(i)) {
				toReturn = false;
			}
		}

		return toReturn;
	}

	// ========================
	// Sorting
	// ========================

	public static void sortRecipes(RecipeSortType sortType) {
        SortUtil.sortRecipes(recipes, sortType);
    }

	public static void sortIngredients(IngredientSortType sortType) {
       SortUtil.sortIngredients(ingredients, sortType);
    }

	// ========================
	// Batch Update
	// ========================

	/** Recalculates all recipe costs/prices (e.g. after an ingredient or VAT change). */
	public static void updateRecipes() {
		for (int i = 0; i < recipes.size(); i++) {
			recipes.get(i).update();
		}
	}
}
