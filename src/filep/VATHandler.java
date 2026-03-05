package filep;

/**
 * Holds the four configurable VAT rates (stored as decimals, e.g. 0.23 = 23%).
 * VAT selection is 1-based (1 through 4).
 */
public class VATHandler {
	public static float[] vats = new float[4];

	/** Returns the VAT rate for a 1-based selection (1-4). Defaults to vats[0] if out of range. */
	public static float getVatFromSelection(int selection) {
		if (selection > 4 || selection < 1) {
			return vats[0];
		}

		switch (selection) {
		case 1:
			return vats[0];
		case 2:
			return vats[1];
		case 3:
			return vats[2];
		case 4:
			return vats[3];
		default:
			return vats[0];
		}
	}
}
