package util;

import javax.swing.JComboBox;

/**
 * Utility methods for JComboBox items that use a numbered prefix format (e.g.
 * "1. Flour").
 */
public class ComboBoxUtil {

	/**
	 * Strips the "N. " prefix from a combo box item string, returning just the
	 * name.
	 */
	public static String stripPrefix(String item) {
		if (item == null)
			return null;
		int dot = item.indexOf(".");
		if (dot < 0)
			return item;
		return item.substring(dot + 1).trim();
	}

	/** Selects the combo box item whose stripped name matches the given name. */
	public static void selectByName(JComboBox<String> comboBox, String name, String newItemLabel) {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			String item = comboBox.getItemAt(i);
			if (item.equals(newItemLabel))
				continue;
			if (stripPrefix(item).equals(name)) {
				comboBox.setSelectedItem(item);
				return;
			}
		}
	}
}
