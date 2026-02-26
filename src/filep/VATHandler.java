package filep;

public class VATHandler {
	public static float[] vats = new float[4];

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
