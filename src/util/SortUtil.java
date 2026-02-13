package util;

import filep.Recipe;
import filep.RecipeSortType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import filep.Ingredient;
import filep.IngredientSortType;

//Because n is small, We can use Collections.sort as it wont impact running time too much.
public class SortUtil {
	
	public static ArrayList<Recipe> sortRecipes(ArrayList<Recipe> toSort,RecipeSortType rst){
		ArrayList<Recipe> sorted = toSort; 
		switch (rst) {
        // Default / fallback
        case DEFAULT:
        	//do nothing lol.
        	break;
        	
        // Cost-related
        case BY_COST_ASC:
        	Collections.sort(sorted,Comparator.comparingDouble(Recipe::getCostToMake));
            break;
        case BY_COST_DESC:
        	Collections.sort(sorted,Comparator.comparingDouble(Recipe::getCostToMake).reversed());
            break;
        case BY_SELLPOINT_ASC:
        	Collections.sort(sorted,Comparator.comparingDouble(Recipe::getSellPoint));
            break;
        case BY_SELLPOINT_DESC:
        	Collections.sort(sorted,Comparator.comparingDouble(Recipe::getSellPoint).reversed());
            break;

        // Name-related
        case BY_NAME_ASC:
        	Collections.sort(sorted, Comparator.comparing(Recipe::getName, String.CASE_INSENSITIVE_ORDER));
            break;
        case BY_NAME_DESC:
        	Collections.sort(sorted, Comparator.comparing(Recipe::getName, String.CASE_INSENSITIVE_ORDER).reversed());
            break;

        // Profit / margin
        case BY_PROFIT_ASC:
        	Collections.sort(sorted, Comparator.comparingDouble(Recipe::getSaleDiff));
            break;
        case BY_PROFIT_DESC:
        	Collections.sort(sorted, Comparator.comparingDouble(Recipe::getSaleDiff).reversed());
            break;
        case BY_MARGIN_ASC:
        	Collections.sort(sorted, Comparator.comparingDouble(Recipe::getProfitMargin));
            break;
        case BY_MARGIN_DESC:
        	Collections.sort(sorted, Comparator.comparingDouble(Recipe::getProfitMargin).reversed());
            break;

        // Ingredient count
        case BY_INGREDIENT_COUNT_ASC:
        	Collections.sort(sorted, Comparator.comparingInt(Recipe::getIngredientAmount));
            break;
        case BY_INGREDIENT_COUNT_DESC:
        	Collections.sort(sorted, Comparator.comparingInt(Recipe::getIngredientAmount).reversed());
            break;
		}
		
		return sorted;
	}
	
	public static ArrayList<Ingredient> sortIngredients(ArrayList<Ingredient> toSort,IngredientSortType rst) {
		ArrayList<Ingredient> sorted = toSort;
		
        switch (rst) {
            // Default / fallback
            case DEFAULT:
                break;

            // Cost-related
            case BY_COST_ASC:
            	Collections.sort(sorted,Comparator.comparingDouble(Ingredient::getCost));
                break;
            case BY_COST_DESC:
            	Collections.sort(sorted,Comparator.comparingDouble(Ingredient::getCost).reversed());
                break;

            // Name-related
            case BY_NAME_ASC:
            	Collections.sort(sorted, Comparator.comparing(Ingredient::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case BY_NAME_DESC:
            	Collections.sort(sorted, Comparator.comparing(Ingredient::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;

            // ID-related
            case BY_ID_ASC:
            	Collections.sort(sorted,Comparator.comparingInt(Ingredient::getID));
                break;
            case BY_ID_DESC:
            	Collections.sort(sorted,Comparator.comparingInt(Ingredient::getID).reversed());
                break;
        }
        
        return sorted;
    }
}
