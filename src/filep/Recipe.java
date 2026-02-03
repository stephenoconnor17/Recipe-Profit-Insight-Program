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
	
	private Object[] displayArr = new Object[4];
	
	public Recipe(String name, float sellPoint) {
		this.name = name;
		this.costToMake = 0;
		this.sellPoint = sellPoint;
		
		updateSaleDiff();
		updateDisplayArr();
		
		this.recipeIngredients = new ArrayList<Ingredient>();
		this.ingredientUnitCost = new HashMap<>();
		
	}
	
	public Object[] getDisplayArr() {
		return this.displayArr;
	}
	
	public void updateDisplayArr() {
		this.displayArr[0] = this.name;
		this.displayArr[1] = this.costToMake;
		this.displayArr[2] = this.sellPoint;
		this.displayArr[3] = this.saleDiff;
	}
	
	public void updateSaleDiff() {
		this.saleDiff = this.sellPoint - this.costToMake;
	}
	
	//ingriedient addition
	public void addIngredient(Ingredient ingredient, float unitsInGrams) {
		this.recipeIngredients.add(ingredient);
		this.ingredientUnitCost.put(ingredient, (ingredient.getCostPer1g() * unitsInGrams));
		this.ingredientGramsUsed.put(ingredient, unitsInGrams);
		this.costToMake += (ingredient.getCostPer1g() * unitsInGrams);
		
		updateSaleDiff();
		updateDisplayArr();
	}
	
	/*
	public void addIngredient(Ingredient ingredient, float unitsInGrams) {
		this.recipeIngredients.add(ingredient);
		this.ingredientUnitCost.put(ingredient, (ingredient.getCostPer1g() * unitsInGrams));
		this.costToMake += (ingredient.getCostPer1g() * unitsInGrams);
		
		updateSaleDiff();
		updateDisplayArr();
	}*/
	
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
		
		if(this.costToMake < 0) {
			this.costToMake = 0;
		}
		
		updateSaleDiff();
		updateDisplayArr();
	}

	
	//getter/setter
	public void setName(String name) {
		this.name = name;
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
	
	public ArrayList<Ingredient> getIngredients(){
		return this.recipeIngredients;
	}
	
	public float getCostOfIngredientInRecipe(Ingredient i) {
		return ingredientUnitCost.get(i);
	}
	
	public float getGramsUsedOfIngredient(Ingredient i) {
		return ingredientGramsUsed.get(i);
	}
}
