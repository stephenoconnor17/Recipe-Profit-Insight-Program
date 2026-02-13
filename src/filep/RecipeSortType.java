package filep;

public enum RecipeSortType {
	DEFAULT,

	// Cost-related
	BY_COST_ASC, BY_COST_DESC,BY_SELLPOINT_ASC,BY_SELLPOINT_DESC,

	// Name-related
	BY_NAME_ASC, BY_NAME_DESC,

	// Profit / margin
	BY_PROFIT_ASC, BY_PROFIT_DESC, BY_MARGIN_ASC, BY_MARGIN_DESC,

	// Ingredient count
	BY_INGREDIENT_COUNT_ASC, BY_INGREDIENT_COUNT_DESC;
/*
	// Filtering-style views
	SHOW_ALL, SHOW_PROFITABLE, SHOW_UNPROFITABLE;
	*/
	public String toString() {
		return name().replace("BY_","" ).replace("_"," ").toUpperCase();
	}
}
