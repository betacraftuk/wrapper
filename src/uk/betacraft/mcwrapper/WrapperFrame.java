package uk.betacraft.mcwrapper;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;

import javax.swing.JFrame;

public class WrapperFrame extends JFrame {
	private static final long serialVersionUID = 2031802022722032802L;

	private String[] known_main_classes = new String[] {
			"com.mojang.minecraft.MinecraftApplet", // classic
			"net.minecraft.client.MinecraftApplet", // indev - 1.5.2
			"M" // 4k
	};

	public String window_name;
	public Image icon;

	public int width;
	public int height;
	public String main_class_path;

	private ClassLoader class_loader;
	public HashMap<String, String> parameters;
	public static Wrapper wrapper;

	public WrapperFrame(String name, Image icon, int x, int y, boolean maximize, boolean resizeable, HashMap<String, String> parameters, String main_class_path) {
		this.window_name = name;
		this.icon = icon;

		this.width = x;
		this.height = y;
		this.main_class_path = main_class_path;

		this.parameters = parameters;

		try {
			this.class_loader = this.getClass().getClassLoader();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		this.make(maximize, resizeable);
	}

	private void make(boolean maximize, boolean resizeable) {
		this.setTitle(this.window_name);

		if (this.icon != null)
			this.setIconImage(this.icon);

		this.getContentPane().setBackground(Color.BLACK);
		this.getContentPane().setLayout(new BorderLayout());

		if (maximize)
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		this.setResizable(resizeable);

		if (!BCWrapper.arguments.containsKey("invisible"))
			this.setVisible(true);
	}

	public void replace(Applet applet) {
		this.getContentPane().removeAll();
		wrapper = this.makeWrapper(applet);
		this.getContentPane().add(wrapper, "Center");

		this.pack();
		this.setLocationRelativeTo(null);

		wrapper.init();
		wrapper.start();
		this.validate();
	}

	private Wrapper makeWrapper(Applet ready) {
		if (ready == null) {
			Class<?> main_class = null;
			Throwable[] errors = new Throwable[2];
			try {
				main_class = this.class_loader.loadClass(this.main_class_path);
			} catch (Throwable t) {
				errors[0] = t;
				for (int i = 0; i < this.known_main_classes.length; i++) {
					try {
						main_class = this.class_loader.loadClass(this.known_main_classes[i]);
						break;
					} catch (Throwable t1) {
						errors[1] = t1;
					}
				}
			}
			if (main_class == null) {
				System.out.println("Could not find the applet class for the game jar!");
				errors[0].printStackTrace();
				errors[1].printStackTrace();
				return null;
			}
			try {
				ready = (Applet) main_class.newInstance();
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		}
		try {
			Wrapper wrapper = new Wrapper(ready, this.width, this.height);
			wrapper.applet_parameters = this.parameters;
			return wrapper;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
}
