package filep;

/** Sort options available for the ingredient list. */
public enum IngredientSortType {
	//default
	DEFAULT,

	//ingredient cost
	BY_COST_ASC, BY_COST_DESC,

	//name
	BY_NAME_ASC, BY_NAME_DESC,

	//by id (basically shows which is oldest to newest
	BY_ID_ASC, BY_ID_DESC;

	@Override
	public String toString() {
		return name().replace("BY_", "").replace("_", " ").toUpperCase();
	}
}
