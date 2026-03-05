package filep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe {
	private String name;
	private float costToMake;
	private float sellPoint;
	private float saleDiff;
	private float markUp;
	private float ingredientCostPercentage;
	private float profitMargin;
	
	private int vatSelection;
	
	private float manPowerCost;
	private float electricityCost;
	private float packagingCost;

	private ArrayList<Ingredient> recipeIngredients;
	private Map<Ingredient, Float> ingredientUnitCost;
	private Map<Ingredient, Float> ingredientGramsUsed;

	private Object[] displayArr = new Object[6];

	public Recipe(String name) {
		this.name = name;
		this.costToMake = 0;
		this.sellPoint = 0;
		this.ingredientCostPercentage = 0;
		this.profitMargin = 0;
		this.markUp = 0;
		
		this.vatSelection = 1;
		
		this.packagingCost = 0;
		this.electricityCost = 0;
		this.manPowerCost = 0;

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
		this.displayArr[0] = this.name;
		this.displayArr[1] = String.format("€%.2f", this.costToMake);
		this.displayArr[2] = String.format("€%.2f", this.sellPoint);
		String prefix = saleDiff > 0 ? "+" : "-";
		this.displayArr[3] = String.format("%s€%.2f", prefix, Math.abs(this.saleDiff));
		this.displayArr[4] = String.format("%s%.2f%%", prefix,Math.abs(ingredientCostPercentage));
		this.displayArr[5] = String.format("%s%.2f%%", prefix,Math.abs(profitMargin));
	
	}
	
	public void setMarkUp(float markup) {
		this.markUp = markup;
		update();
	}
	
	public float getMarkUp() {
		return this.markUp;
	}
	
	public void setVatSelection(int selection) {
		if(selection >= 1 && selection <= 4) {
			this.vatSelection  = selection;
			update();
		}
	}
	
	public int getVatSelection() {
		return this.vatSelection;
	}
	
	public void updateSellPointAfterVat() {
		this.sellPoint = (this.costToMake * (1 + (markUp / 100))) * (1 + VATHandler.getVatFromSelection(vatSelection));
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

	public void addIngredient(Ingredient ingredient, float unitsInGrams) {
		this.recipeIngredients.add(ingredient);
		this.ingredientUnitCost.put(ingredient, ((ingredient.getCostAfterVat() / ingredient.getGrams() ) * unitsInGrams ));
		this.ingredientGramsUsed.put(ingredient, unitsInGrams);
		this.costToMake += unitsInGrams * (ingredient.getCostAfterVat() / ingredient.getGrams());
		update();
	}

	public int returnIngredientListSize() {
		return this.recipeIngredients.size();
	}

	public Ingredient getIngredientFromList(int i) {
		return this.recipeIngredients.get(i);
	}

	public void removeIngredient(Ingredient ingredient) {
		this.recipeIngredients.remove(ingredient);
		this.ingredientUnitCost.remove(ingredient);
		this.ingredientGramsUsed.remove(ingredient);

		update();
	}

	public void setName(String name) {
		RecipeHandler.recipeByName.remove(this.name);
		this.name = name;
		RecipeHandler.recipeByName.put(this.name, this);
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
	
	public float getElectricityCost() {
		return this.electricityCost;
	}
	
	public float getManPowerCost() {
		return this.manPowerCost;
	}
	
	public float getPackagingCost() {
		return this.packagingCost;
	}
	
	public void setElectricityCost(float electricityCost) {
		this.electricityCost = electricityCost;
		update();
	}
	
	public void setManPowerCost(float manPowerCost) {
		this.manPowerCost = manPowerCost;
		update();
	}
	
	public void setPackagingCost(float packagingCost) {
		this.packagingCost = packagingCost;
		update();
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
		costToMake += electricityCost;
		costToMake += manPowerCost;
		costToMake += packagingCost;
		for (Ingredient i : recipeIngredients) {
			float gramsUsed = ingredientGramsUsed.get(i);
			this.costToMake += gramsUsed * (i.getCostAfterVat() / i.getGrams());
			
		}
	}

	public void update() {
		updateCostToMake();
		updateSellPointAfterVat();
		updateSaleDiff();
		updateDisplayArr();
	}

	public boolean compareToRecipe(Recipe i) {
		boolean toReturn = false;

		if (i.getName().equals(this.getName()) && this.getSellPoint() == i.getSellPoint()) {
			toReturn = true;
		}

		return toReturn;
	}
	
	public int getIngredientAmount() {
		return this.recipeIngredients.size();
	}
}
