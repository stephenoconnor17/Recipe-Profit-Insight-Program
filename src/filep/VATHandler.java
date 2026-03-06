package filep;

/**
 * Holds the four configurable VAT rates (stored as decimals, e.g. 0.23 = 23%).
 * VAT selection is 1-based (1 through 4).
 */
public class VATHandler {
	public static float[] vats = new float[4];

	/**
	 * Returns the VAT rate for a 1-based selection (1-4). Defaults to vats[0] if
	 * out of range.
	 */
	public static float getVatFromSelection(int selection) {
		if (selection >= 1 && selection <= 4) {
			return vats[selection - 1];
		}
		return vats[0];
	}
}
