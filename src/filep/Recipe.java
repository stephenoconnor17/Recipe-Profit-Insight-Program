package filep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a recipe consisting of ingredients and overhead costs.
 * Tracks cost-to-make, sell point (with VAT + markup), profit margin, and display data.
 */
public class Recipe {

	// ========================
	// Fields
	// ========================

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

	/** Pre-formatted display data for the main recipe table. */
	private Object[] displayArr = new Object[9];

	// ========================
	// Constructor
	// ========================

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

	// ========================
	// Name
	// ========================

	public String getName() {
		return this.name;
	}

	/** Updates the name and re-keys this recipe in RecipeHandler's lookup map. */
	public void setName(String name) {
		RecipeHandler.recipeByName.remove(this.name);
		this.name = name;
		RecipeHandler.recipeByName.put(this.name, this);
		updateDisplayArr();
	}

	// ========================
	// VAT & Markup
	// ========================

	public int getVatSelection() {
		return this.vatSelection;
	}

	public void setVatSelection(int selection) {
		if(selection >= 1 && selection <= 4) {
			this.vatSelection  = selection;
			update();
		}
	}

	public float getMarkUp() {
		return this.markUp;
	}

	public void setMarkUp(float markup) {
		this.markUp = markup;
		update();
	}

	// ========================
	// Cost & Pricing
	// ========================

	public float getCostToMake() {
		return this.costToMake;
	}

	public float getSellPoint() {
		return this.sellPoint;
	}

	public double getSaleDiff() {
		return this.saleDiff;
	}

	public float getIngredientCostPercentage() {
		return this.ingredientCostPercentage;
	}

	public double getProfitMargin() {
		return this.profitMargin;
	}

	// ========================
	// Overhead Costs
	// ========================

	public float getElectricityCost() {
		return this.electricityCost;
	}

	public void setElectricityCost(float electricityCost) {
		this.electricityCost = electricityCost;
		update();
	}

	public float getManPowerCost() {
		return this.manPowerCost;
	}

	public void setManPowerCost(float manPowerCost) {
		this.manPowerCost = manPowerCost;
		update();
	}

	public float getPackagingCost() {
		return this.packagingCost;
	}

	public void setPackagingCost(float packagingCost) {
		this.packagingCost = packagingCost;
		update();
	}

	// ========================
	// Ingredient Management
	// ========================

	public ArrayList<Ingredient> getIngredients() {
		return this.recipeIngredients;
	}

	public int getIngredientAmount() {
		return this.recipeIngredients.size();
	}

	public int returnIngredientListSize() {
		return this.recipeIngredients.size();
	}

	public Ingredient getIngredientFromList(int i) {
		return this.recipeIngredients.get(i);
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

	/** Adds an ingredient with the specified grams used, updates cost maps, and recalculates totals. */
	public void addIngredient(Ingredient ingredient, float unitsInGrams) {
		this.recipeIngredients.add(ingredient);
		this.ingredientUnitCost.put(ingredient, ((ingredient.getCostAfterVat() / ingredient.getGrams() ) * unitsInGrams ));
		this.ingredientGramsUsed.put(ingredient, unitsInGrams);
		this.costToMake += unitsInGrams * (ingredient.getCostAfterVat() / ingredient.getGrams());
		update();
	}

	/** Removes an ingredient and its cost/grams entries, then recalculates totals. */
	public void removeIngredient(Ingredient ingredient) {
		this.recipeIngredients.remove(ingredient);
		this.ingredientUnitCost.remove(ingredient);
		this.ingredientGramsUsed.remove(ingredient);

		update();
	}

	// ========================
	// Recalculation Methods
	// ========================

	/** Master update: recalculates cost, sell point, sale diff, and display data. */
	public void update() {
		updateCostToMake();
		updateSellPointAfterVat();
		updateSaleDiff();
		updateDisplayArr();
	}

	/** Sums overhead costs + ingredient costs to derive total costToMake. */
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

	/** Calculates sell point from costToMake, markup percentage, and VAT rate. */
	public void updateSellPointAfterVat() {
		this.sellPoint = (this.costToMake * (1 + (markUp / 100))) * (1 + VATHandler.getVatFromSelection(vatSelection));
	}

	/** Derives saleDiff, ingredientCostPercentage, and profitMargin from cost and sell point. */
	public void updateSaleDiff() {
		this.saleDiff = this.sellPoint - this.costToMake;
		this.ingredientCostPercentage = (costToMake / sellPoint) * 100;
		this.profitMargin = (sellPoint - costToMake) / sellPoint * 100;
	}

	// ========================
	// Display
	// ========================

	public Object[] getDisplayArr() {
		return this.displayArr;
	}

	/** Formats recipe data into displayArr for the main table view. */
	public void updateDisplayArr() {
		this.displayArr[0] = this.name;
		this.displayArr[1] = String.format("€%.2f", this.costToMake);
		this.displayArr[2] = String.format("%.2f%%", this.markUp);
		this.displayArr[3] = String.format("%.2f%%", VATHandler.getVatFromSelection(vatSelection) * 100);
		float priceBeforeVat = this.costToMake * (1 + (markUp / 100));
		float vatPaid = this.sellPoint - priceBeforeVat;
		this.displayArr[4] = String.format("€%.2f", vatPaid);
		this.displayArr[5] = String.format("€%.2f", this.sellPoint);
		String prefix = saleDiff > 0 ? "+" : "-";
		this.displayArr[6] = String.format("%s€%.2f", prefix, Math.abs(this.saleDiff));
		this.displayArr[7] = String.format("%s%.2f%%", prefix,Math.abs(profitMargin));
		this.displayArr[8] = String.format("%s%.2f%%", prefix,Math.abs(ingredientCostPercentage));

	}

	// ========================
	// Comparison
	// ========================

	/** Checks if this recipe matches another by name. Used to prevent duplicates. */
	public boolean compareToRecipe(Recipe i) {
		return i.getName().equals(this.getName());
	}
}
