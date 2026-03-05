package filep;

/** Sort options available for the recipe list. */
public enum RecipeSortType {
	DEFAULT,

	BY_COST_ASC, BY_COST_DESC,
	BY_SELLPOINT_ASC, BY_SELLPOINT_DESC,

	BY_NAME_ASC, BY_NAME_DESC,

	BY_PROFIT_ASC, BY_PROFIT_DESC,
	BY_MARGIN_ASC, BY_MARGIN_DESC,

	BY_INGREDIENT_COUNT_ASC, BY_INGREDIENT_COUNT_DESC;

	public String toString() {
		return name().replace("BY_","" ).replace("_"," ").toUpperCase();
	}
}
