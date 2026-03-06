package mainp;

import javax.swing.UIManager;

import myGUI.MyFrame;

/** Application entry point. Launches the main Recipe Book window. */
public class Main {

	public static void main(String[] args) {
		//Basically open a socket to run this process locally.
		//if process is running instance is running so block another instance.
		//this is because of static RecipeHandler that would be corrupted on heap by other instances.
		try {
			new java.net.ServerSocket(49152, 0, java.net.InetAddress.getLoopbackAddress());
		} catch (java.io.IOException e) {
			javax.swing.JOptionPane.showMessageDialog(null, "Recipe Profitizer is already running.");
			System.exit(0);
		}

		MyFrame mf = new MyFrame();
	}

}
