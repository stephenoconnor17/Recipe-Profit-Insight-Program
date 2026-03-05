package util;

import javax.swing.JComboBox;

public class ComboBoxUtil {

	public static String stripPrefix(String item) {
		if (item == null) return null;
		int dot = item.indexOf(".");
		if (dot < 0) return item;
		return item.substring(dot + 1).trim();
	}

	public static void selectByName(JComboBox<String> comboBox, String name, String newItemLabel) {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			String item = comboBox.getItemAt(i);
			if (item.equals(newItemLabel)) continue;
			if (stripPrefix(item).equals(name)) {
				comboBox.setSelectedItem(item);
				return;
			}
		}
	}
}
