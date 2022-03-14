package uk.betacraft.mcwrapper;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class GameAppletLauncher {

	public static WrapperFrame wrapper_frame;

	public GameAppletLauncher(HashMap<String, String> parameters, String main_class_path, String frame_name, int x, int y, boolean maximize, Image icon) {
		wrapper_frame = new WrapperFrame(frame_name, icon, x, y, maximize, parameters, main_class_path);
	}

	public void launchGame() {
		wrapper_frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				wrapper_frame.setVisible(false);
				wrapper_frame.dispose();
			}
		});
		wrapper_frame.replace(null);
	}
}
