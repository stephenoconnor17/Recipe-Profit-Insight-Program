package filep;

public class Ingredient {
	private String name;
	private String supplier;
	private float costPer100g;
	private float grams;
	private int id;

	public static final int NEW_ID_SENTINEL = 0;

	// PASS THROUGH THE COST AND GRAMS.
	public Ingredient(String name, String supplier, float cost, float grams, int id) {
		this.name = name;
		this.supplier = supplier.toLowerCase();
		this.grams = grams;
		if (grams > 0) {
			this.costPer100g = (cost / grams) * 100;
		} else {
			this.costPer100g = 0f;
		}

		if (id == Ingredient.NEW_ID_SENTINEL) {
			this.id = RecipeHandler.nextAvailableID;
			while (RecipeHandler.ingredientIDMap.get(this.id) != null) {
				RecipeHandler.nextAvailableID++;
				this.id = RecipeHandler.nextAvailableID;
			}
		} else {
			this.id = id;
		}
	}

	public void setSupplierName(String name) {
		this.supplier = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getSupplierName() {
		return this.supplier;
	}

	public float getCostPer100g() {
		return this.costPer100g;
	}

	public float getCostPer1g() {
		return this.costPer100g / 100;
	}

	public float getCostPer1kg() {
		return this.costPer100g * 10;
	}

	public void setCostPer100g(float cost) {
		this.costPer100g = cost;
	}

	public void setCostPer1g(float cost) {
		this.costPer100g = cost * 100f;
	}

	public void setCostPer1kg(float cost) {
		this.costPer100g = cost / 10;
	}

	public float getGrams() {
		return this.grams;
	}

	public void setGrams(float grams) {
		this.grams = grams;
	}

	public int getID() {
		return this.id;
	}

	public void setCost(float cost) {
		this.costPer100g = (cost / grams) * 100;
	}
	
	public double getCost() {
		return this.getCostPer1g() * grams;
	}

	@Override
	public String toString() {
	    java.text.DecimalFormat df = new java.text.DecimalFormat("0.0000"); //THIS is to prevent scientific notation formatting.

	    return "NAME: " + getName()
	        + " | SUPPLIER: " + getSupplierName()
	        + " | GRAMS PER UNIT: " + getGrams()
	        + " | COST PER UNIT: €" + df.format(getCost())
	        + " | COST PER 1G/100G/1000G: €"
	        + df.format(getCostPer1g()) + " / €"
	        + df.format(getCostPer100g()) + " / €"
	        + df.format(getCostPer1kg());
	}


	public boolean compareToIngredient(Ingredient i) {
		boolean toReturn = false;
		
		String ingredientName = i.getName().toLowerCase();
		String ingredientSupplier = i.getSupplierName().toLowerCase();
		String thisIngredientName = this.name.toLowerCase();
		String thisIngredientSupplier = this.supplier.toLowerCase();
		
		if (i.getID() == this.getID()) {
			toReturn = true;
		}

		if (ingredientName.equals(thisIngredientName) && ingredientSupplier.equals(thisIngredientSupplier)) {
			toReturn = true;
		}

		return toReturn;
	}

}
