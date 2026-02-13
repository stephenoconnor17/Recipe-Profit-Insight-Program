package filep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe {
	private String name;
	private float costToMake;
	private float sellPoint;
	private float saleDiff;
	private float ingredientCostPercentage;
	private float profitMargin;

	private ArrayList<Ingredient> recipeIngredients;
	private Map<Ingredient, Float> ingredientUnitCost;
	private Map<Ingredient, Float> ingredientGramsUsed;

	private Object[] displayArr = new Object[6];

	public Recipe(String name, float sellPoint) {
		this.name = name;
		this.costToMake = 0;
		this.sellPoint = sellPoint;
		this.ingredientCostPercentage = 0;
		this.profitMargin = 0;

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
		// 0 name
		// 1 cost to make
		// 2 selling point
		// 3 the difference of selling point to making cost
		// 4 food cost percentage which is cost of making over selling cost i.e what
		//   percent of recipe sell point returns the value gone into it.
		// 5 profit margin.
		this.displayArr[0] = this.name;
		this.displayArr[1] = String.format("€%.2f", this.costToMake);
		this.displayArr[2] = String.format("€%.2f", this.sellPoint);
		String prefix = saleDiff > 0 ? "+" : "-";
		this.displayArr[3] = String.format("%s€%.2f", prefix, Math.abs(this.saleDiff));
		this.displayArr[4] = String.format("%s%.2f%%", prefix,Math.abs(ingredientCostPercentage));// to show as a percentage
		this.displayArr[5] = String.format("%s%.2f%%", prefix,Math.abs(profitMargin));
	
	}
	
	public float getIngredientCostPercentage() {
		return this.ingredientCostPercentage;
	}

	public void updateSaleDiff() {
		this.saleDiff = this.sellPoint - this.costToMake;
		this.ingredientCostPercentage = (costToMake / sellPoint) * 100;
		this.profitMargin = (sellPoint - costToMake) / sellPoint * 100;
	}
	
	public double getProfitMargin() {
		return this.profitMargin;
	}
	
	public double getSaleDiff() {
		return this.saleDiff;
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
	
	public int getIngredientAmount() {
		return this.recipeIngredients.size();
	}
}
