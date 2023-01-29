package uk.betacraft.mcwrapper;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class GameAppletLauncher {

	public static WrapperFrame wrapper_frame;

	public GameAppletLauncher(HashMap<String, String> parameters, String main_class_path, String frame_name, int x, int y, boolean maximize, boolean resizeable, Image icon) {
		wrapper_frame = new WrapperFrame(frame_name, icon, x, y, maximize, resizeable, parameters, main_class_path);
	}

	public void launchGame() {
		wrapper_frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				(new LoopThread()).start();
				if (WrapperFrame.wrapper != null) {
					WrapperFrame.wrapper.stop();
					WrapperFrame.wrapper.destroy();
				}
				
				System.exit(0);
			}
		});
		wrapper_frame.replace(null);
	}
	
	class LoopThread extends Thread {

		   public void run() {
		      try {
		         Thread.sleep(30000L);
		      } catch (InterruptedException var2) {
		         var2.printStackTrace();
		      }

		      System.out.println("FORCING EXIT!");
		      System.exit(0);
		   }
		}
}
