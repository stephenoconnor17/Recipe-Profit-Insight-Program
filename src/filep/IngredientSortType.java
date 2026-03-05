package filep;

public enum IngredientSortType {
    DEFAULT,


    BY_COST_ASC,
    BY_COST_DESC,


    BY_NAME_ASC,
    BY_NAME_DESC,


    BY_ID_ASC,
    BY_ID_DESC;
	
	@Override
	public String toString() {
		return name().replace("BY_","").replace("_", " ").toUpperCase();
	}
}
