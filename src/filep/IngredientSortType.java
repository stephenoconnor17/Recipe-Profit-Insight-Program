package filep;

public enum IngredientSortType {
    DEFAULT,

    // Cost-related
    BY_COST_ASC,
    BY_COST_DESC,

    // Name-related
    BY_NAME_ASC,
    BY_NAME_DESC,

    // ID-related (if ingredients have IDs)
    BY_ID_ASC,
    BY_ID_DESC
}
