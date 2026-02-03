package filep;

public class Ingredient {
	private String name;
	private String supplier;
	private float costPer100g;
	private float grams;
	private int id;
	
	//PASS THROUGH THE COST AND GRAMS.
	public Ingredient(String name, String supplier, float cost, float grams,int id){
		this.name = name;
		this.supplier = supplier.toLowerCase();
		this.grams = grams;
		if(grams > 0) {
			this.costPer100g = (cost / grams) * 100;
		}else {
			this.costPer100g = 12345f;
		}
		this.id = id;
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
		return this.costPer100g/100;
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
	
	/*
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Name: " + getName() + ", "
	}
	*/
}
