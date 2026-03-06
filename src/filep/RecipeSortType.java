package filep;

/** Sort options available for the recipe list. */
public enum RecipeSortType {
	//default
	DEFAULT,

	//costs
	BY_COST_ASC, BY_COST_DESC, 
	
	//sell-point
	BY_SELLPOINT_ASC, BY_SELLPOINT_DESC,

	//alphabetically named
	BY_NAME_ASC, BY_NAME_DESC,

	//profit margin
	BY_PROFIT_ASC, BY_PROFIT_DESC,
	
	//food margin
	BY_MARGIN_ASC, BY_MARGIN_DESC,

	//how many ingredients
	BY_INGREDIENT_COUNT_ASC, BY_INGREDIENT_COUNT_DESC;

	public String toString() {
		return name().replace("BY_", "").replace("_", " ").toUpperCase();
	}
}
