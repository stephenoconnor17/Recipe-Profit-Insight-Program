package filep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe {
	private String name;
	private float costToMake;
	private float sellPoint;
	private float saleDiff;

	private ArrayList<Ingredient> recipeIngredients;
	private Map<Ingredient, Float> ingredientUnitCost;
	private Map<Ingredient, Float> ingredientGramsUsed;

	private Object[] displayArr = new Object[5];

	public Recipe(String name, float sellPoint) {
		this.name = name;
		this.costToMake = 0;
		this.sellPoint = sellPoint;

		updateSaleDiff();
		updateDisplayArr();

		this.recipeIngredients = new ArrayList<Ingredient>();
		this.ingredientGramsUsed = new HashMap<>();
		this.ingredientUnitCost = new HashMap<>();

	}

	public Object[] getDisplayArr() {
		return this.displayArr;
	}

	public void updateDisplayArr() {
		// 1 name
		// 2 cost to make
		// 3 selling point
		// 4 the difference of selling point to making cost
		// 5 food cost percentage which is cost of making over selling cost i.e what
		// percent of recipe sell point returns the value gone into it.
		this.displayArr[0] = this.name;
		this.displayArr[1] = String.format("€%.2f", this.costToMake);
		this.displayArr[2] = String.format("€%.2f", this.sellPoint);
		String prefix = saleDiff > 0 ? "+" : "-";
		this.displayArr[3] = String.format("%s€%.2f", prefix, Math.abs(this.saleDiff));
		this.displayArr[4] = String.format("%s%.2f%%", prefix, Math.abs((costToMake / sellPoint) * 100));// to show as a
																											// percentage
	}

	public void updateSaleDiff() {
		this.saleDiff = this.sellPoint - this.costToMake;
	}

	// ingriedient addition
	public void addIngredient(Ingredient ingredient, float unitsInGrams) {
		this.recipeIngredients.add(ingredient);
		this.ingredientUnitCost.put(ingredient, (ingredient.getCostPer1g() * unitsInGrams));
		this.ingredientGramsUsed.put(ingredient, unitsInGrams);
		this.costToMake += (ingredient.getCostPer1g() * unitsInGrams);

		updateSaleDiff();
		updateDisplayArr();
	}

	public int returnIngredientListSize() {
		return this.recipeIngredients.size();
	}

	public Ingredient getIngredientFromList(int i) {
		return this.recipeIngredients.get(i);
	}

	public void removeIngredient(Ingredient ingredient) {
		this.recipeIngredients.remove(ingredient);
		this.costToMake -= this.ingredientUnitCost.get(ingredient);
		this.ingredientUnitCost.remove(ingredient);
		this.ingredientGramsUsed.remove(ingredient);

		if (this.costToMake < 0) {
			this.costToMake = 0;
		}

		updateSaleDiff();
		updateDisplayArr();
	}

	// getter/setter
	public void setName(String name) {
		RecipeHandler.recipeByName.remove(this.name);
		this.name = name;
		RecipeHandler.recipeByName.put(this.name, this);
		updateDisplayArr();
	}

	public void setSellPoint(float sellPoint) {
		this.sellPoint = sellPoint;
		updateSaleDiff();
		updateDisplayArr();
	}

	public String getName() {
		return this.name;
	}

	public float getCostToMake() {
		return this.costToMake;
	}

	public float getSellPoint() {
		return this.sellPoint;
	}

	public ArrayList<Ingredient> getIngredients() {
		return this.recipeIngredients;
	}

	public Ingredient getIngredient(Ingredient temp) {
		int i = this.recipeIngredients.indexOf(temp);
		return getIngredientFromList(i);
	}

	public float getCostOfIngredientInRecipe(Ingredient i) {
		return ingredientUnitCost.get(i);
	}

	public float getGramsUsedOfIngredient(Ingredient i) {
		return ingredientGramsUsed.get(i);
	}

	public void updateCostToMake() {
		this.costToMake = 0;
		for (Ingredient i : recipeIngredients) {
			float gramsUsed = ingredientGramsUsed.get(i);
			this.costToMake += gramsUsed * i.getCostPer1g();
		}
	}

	public void update() {
		updateCostToMake();
		updateSaleDiff();
		updateDisplayArr();
	}

	public boolean compareToRecipe(Recipe i) {
		boolean toReturn = false;

		if (i.getName() == this.getName() && this.getSellPoint() == i.getSellPoint()) {
			toReturn = true;
		}

		return toReturn;
	}
}
