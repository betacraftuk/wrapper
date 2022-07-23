package uk.betacraft.mcwrapper;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.ImageIO;

// Compatible with java 1.5
public class BCWrapper {
	public static HashMap<String, String> arguments = new HashMap<String, String>();

	public static boolean debug = false;

	public static void main(String[] args) {
		final Properties p = System.getProperties();

		for (int i = 0; i < args.length; i++) {
			String[] split = args[i].split("=");
			//System.out.println(args[i]);
			arguments.put(split[0], split.length == 1 ? "" : split[1]);
		}

		debug = "true".equalsIgnoreCase(arguments.get("debug"));

		HashMap<String, String> applet_params = new HashMap<String, String>();
		applet_params.put("username", arguments.get("username"));
		applet_params.put("sessionid", arguments.get("sessionid"));
		applet_params.put("haspaid", "true");

		// Demo state priority:
		// 1. launcher
		// 2. user
		if (arguments.get("demo") != null || System.getProperty("betacraft.demo", "false").equalsIgnoreCase("true")) {
			applet_params.put("demo", "true");
		}

		Iterator<Object> it = p.keySet().iterator();
		while (it.hasNext()) {
			String query = (String) it.next();

			applet_params.put(query, System.getProperty(query));

			if (debug)
				System.out.println("SET " + query + "=" + System.getProperty(query));
		}

		System.out.println("Accepted username: " + arguments.get("username"));

		int x = 854;
		int y = 480;
		try {
			x = Integer.parseInt(arguments.get("width"));
			y = Integer.parseInt(arguments.get("height"));
		} catch (Throwable t) {
			System.out.println("Couldn't parse width/height values. Will use the default ones.");
			arguments.put("width", Integer.toString(x));
			arguments.put("height", Integer.toString(y));
		}

		boolean maximize = arguments.containsKey("maximize");

		String frame_name = (String) arguments.get("frameName");
		if (frame_name == null || frame_name.equals("")) frame_name = "Minecraft";

		String version_name = (String) arguments.get("versionName");
		if (version_name != null && !version_name.equals("")) frame_name = frame_name + " [" + version_name.replaceAll("IJ ", "") + "]";

		Image icon = null;
		if (arguments.containsKey("favicon")) {
			// icon stays null if "none" specified
			String favicon = arguments.get("favicon");
			if (!favicon.equalsIgnoreCase("none")) {
				File fav = new File(favicon);

				if (fav.exists() && fav.isFile()) {
					try {
						icon = ImageIO.read(new FileInputStream(fav));
					} catch (Throwable t) {
						t.printStackTrace();
						System.err.println("Couldn't read favicon!");
					}
				} else {
					System.err.println("Couldn't read favicon!");
				}
			}
		} else {
			try {
				icon = ImageIO.read(BCWrapper.class.getClassLoader().getResourceAsStream("favicon.png"));
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println("Couldn't read default favicon!");
			}
		}

		String main_class = arguments.get("mainClass"); // can be null
		String resizeflag = arguments.get("resizeable"); // can be null
		boolean resizeable = resizeflag != null ? !resizeflag.equals("false") : true;
		GameAppletLauncher applet_launcher = new GameAppletLauncher(applet_params, main_class, frame_name, x, y, maximize, resizeable, icon);
		applet_launcher.launchGame();
	}
}
