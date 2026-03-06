package filep;

/**
 * Represents a single ingredient with its name, supplier, cost, weight, and VAT
 * info. Cost is stored internally as cost-per-100g and converted as needed.
 */
public class Ingredient {

	// ========================
	// Fields & Constants
	// ========================

	private String name;
	private String supplier;
	private float costPer100g;
	private float costAfterVAT;
	private int vatSelection;
	private float grams;
	private int id;

	/** Sentinel value passed as ID when creating a brand-new ingredient. */
	public static final int NEW_ID_SENTINEL = 0;

	// ========================
	// Constructor
	// ========================

	/**
	 * Creates an ingredient. If {@code id} equals {@link #NEW_ID_SENTINEL}, a new
	 * unique ID is assigned via {@link RecipeHandler#nextAvailableID}.
	 *
	 * @param cost  total cost for the given weight (not per-gram)
	 * @param grams weight of one purchased unit
	 */
	public Ingredient(String name, String supplier, float cost, float grams, int id, int newVatSelection) {
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

		setVatSelection(newVatSelection);
	}

	// ========================
	// Name
	// ========================

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// ========================
	// Supplier
	// ========================

	public String getSupplierName() {
		return this.supplier;
	}

	public void setSupplierName(String name) {
		this.supplier = name;
	}

	// ========================
	// ID
	// ========================

	public int getID() {
		return this.id;
	}

	// ========================
	// Weight (grams)
	// ========================

	public float getGrams() {
		return this.grams;
	}

	public void setGrams(float grams) {
		this.grams = grams;
	}

	// ========================
	// Cost accessors (various units)
	// ========================

	/** Returns the total cost for one purchased unit (costPer1g * grams). */
	public double getCost() {
		return this.getCostPer1g() * grams;
	}

	/** Sets costPer100g derived from total cost and current grams. */
	public void setCost(float cost) {
		this.costPer100g = (cost / grams) * 100;
	}

	public float getCostPer1g() {
		return this.costPer100g / 100;
	}

	public void setCostPer1g(float cost) {
		this.costPer100g = cost * 100f;
	}

	public float getCostPer100g() {
		return this.costPer100g;
	}

	public void setCostPer100g(float cost) {
		this.costPer100g = cost;
	}

	public float getCostPer1kg() {
		return this.costPer100g * 10;
	}

	public void setCostPer1kg(float cost) {
		this.costPer100g = cost / 10;
	}

	// ========================
	// VAT
	// ========================

	public int getVatSelection() {
		return this.vatSelection;
	}

	public void setVatSelection(int selection) {
		if (selection >= 1 && selection <= 4) {
			this.vatSelection = selection;
			updateCostAfterVat();
		}
	}

	public float getCostAfterVat() {
		return this.costAfterVAT;
	}

	/** Recalculates costAfterVAT based on current cost, grams, and VAT rate. */
	public void updateCostAfterVat() {
		this.costAfterVAT = (getCostPer1g() * grams) * (1 + VATHandler.getVatFromSelection(vatSelection));
	}

	// ========================
	// Comparison & Display
	// ========================

	/**
	 * Checks if this ingredient matches another by ID or by name+supplier. Used to
	 * prevent duplicate ingredients.
	 */
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

	@Override
	public String toString() {
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.0000");

		return "NAME: " + getName() + " | SUPPLIER: " + getSupplierName() + " | GRAMS PER UNIT: " + getGrams()
				+ " | COST PER UNIT: €" + df.format(getCost()) + " | COST PER 1G/100G/1000G: €"
				+ df.format(getCostPer1g()) + " / €" + df.format(getCostPer100g()) + " / €"
				+ df.format(getCostPer1kg());
	}

}
