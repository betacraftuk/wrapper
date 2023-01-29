package net.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import legacyfix.LegacyURLStreamHandlerFactory;
import uk.betacraft.mcwrapper.BCWrapper;
import uk.betacraft.mcwrapper.applet.AppletUtils;

public class Launcher extends Applet implements AppletStub {
	private static final long serialVersionUID = 2031802022722032801L;

	public Map<String, String> applet_parameters = new HashMap<String, String>();
	public Applet game_applet;
	public int width;
	public int height;

	private boolean active = false;
	private int context = 0;

	public Launcher(Applet game_applet, int x, int y) {
		this.width = x;
		this.height = y;

		this.game_applet = game_applet;

		this.game_applet.setStub(this);
		this.game_applet.setSize(this.width, this.height);

		this.setLayout(new BorderLayout());
		this.add(this.game_applet, "Center");
	}

	public boolean setAppletParameter(String param, String value) {
		String lastValue = this.getParameter(param);
		if (lastValue != null)
			System.out.println("Warning: The applet parameter \"" + param + "\" was overwrote from \"" + lastValue + "\" to \"" + value + "\".");
		return this.setAppletParameter(param, value, true);
	}

	public boolean setAppletParameter(String param, String value, boolean force) {
		if (applet_parameters.containsKey(param) && !force) {
			return false;
		} else {
			applet_parameters.put(param, value);
			return true;
		}
	}

	public String getParameter(String param) {
		if (this.active) System.out.println("Client asked for parameter: " + param);
		if (!applet_parameters.containsKey(param)) {
			return null;
		} else {
			return (String) applet_parameters.get(param);
		}
	}

	// Forge... d-_-b
	public void replace(Applet game_applet) {
		this.game_applet = game_applet;

		this.game_applet.setStub(this);
		this.game_applet.setSize(this.width, this.height);

		this.setLayout(new BorderLayout());
		this.add(this.game_applet, "Center");
		
		this.init();
		this.start();
		this.validate();
	}

	public void init() {
		this.active = true;
		this.game_applet.init();

		// Allow for runs without LegacyFix
		if (!"true".equals(System.getProperty("lf.present", "false"))) {
			applyFixes();
		}
	}

	public void applyFixes() {
		System.out.println("Applying fixes...");
		// Apply any necessary fixes
		boolean disable_appletMode = System.getProperty("betacraft.disable_appletMode") != null;
		boolean force_resolution = System.getProperty("betacraft.force_resolution") != null;
		boolean force_fullscreen = System.getProperty("betacraft.force_fullscreen") != null;
		boolean cant_accept_server_from_applet = System.getProperty("betacraft.cant_accept_server_from_applet") != null;

		if (disable_appletMode || force_fullscreen) {
			System.out.println("Disabling appletMode...");
			boolean result = AppletUtils.disableAppletMode(
					this.game_applet,
					System.getProperty("betacraft.applet.game_field"),
					System.getProperty("betacraft.applet.appletMode_field")
					);
			System.out.println(result ? "Disabled appletMode." : "Failed to disable appletMode");
		}

		if (force_resolution) {
			System.out.println("Forcing custom resolution...");
			boolean result = AppletUtils.setResolution(
					this.game_applet,
					System.getProperty("betacraft.applet.game_field"),
					System.getProperty("betacraft.applet.width_field"),
					System.getProperty("betacraft.applet.height_field"),
					this.width,
					this.height
					);
			System.out.println(result ? "Forced custom resolution." : "Failed to force resolution");
		}

		if (force_fullscreen) {
			System.out.println("Forcing fullscreen...");
			boolean result = AppletUtils.setFullscreen(
					this.game_applet,
					System.getProperty("betacraft.applet.game_field"),
					System.getProperty("betacraft.applet.fullscreen_field"),
					System.getProperty("betacraft.applet.canvas_field"),
					true
					);
			System.out.println(result ? "Forced fullscreen." : "Failed to force fullscreen");
		}

		if (cant_accept_server_from_applet) {
			System.out.println("Manually setting server...");
			boolean result = AppletUtils.setServer(
					this.game_applet,
					System.getProperty("betacraft.applet.game_field"),
					System.getProperty("betacraft.applet.server_field"),
					System.getProperty("betacraft.server_class"),
					System.getProperty("betacraft.server_constructor_arguments")
					);
			System.out.println(result ? "Set the server." : "Failed to set the server");
		}
	}

	public void start() {
		try {
			URL.setURLStreamHandlerFactory(new LegacyURLStreamHandlerFactory());
		} catch (Throwable err) {
			// already defined. this will only throw in forge situation
		}
		this.game_applet.start();
	}

	public void stop() {
		this.active = false;
		this.game_applet.stop();
	}

	public void destroy() {
		this.game_applet.destroy();
	}

	public boolean isActive() {
		return active;
	}

	public URL getDocumentBase() {
		try {
			int port = (this.game_applet.getClass().getCanonicalName().startsWith("com.mojang") ? 80 : -1);
			URL url = new URL("http", "www.minecraft.net", port, "/game/");
			return url;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public URL getCodeBase() {
		try {
			return new URL("http", "www.minecraft.net", 80, "/game/");
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	@Override
	public java.awt.Dimension getMinimumSize() {
		String widthstr = BCWrapper.arguments.get("minWidth");
		String heightstr = BCWrapper.arguments.get("minHeight");

		Integer width = null;
		if (widthstr != null) {
			width = Integer.parseInt(widthstr);
		}

		Integer height = null;
		if (heightstr != null) {
			height = Integer.parseInt(heightstr);
		}

		if (width != null && height != null) {
			return new java.awt.Dimension(width, height);
		} else if (width != null) {
			return new java.awt.Dimension(width, (int)super.getMinimumSize().getHeight());
		} else if (height != null) {
			return new java.awt.Dimension((int)super.getMinimumSize().getWidth(), height);
		} else {
			return super.getMinimumSize();
		}
	}

	@Override
	public java.awt.Dimension getMaximumSize() {
		String widthstr = BCWrapper.arguments.get("maxWidth");
		String heightstr = BCWrapper.arguments.get("maxHeight");

		Integer width = null;
		if (widthstr != null) {
			width = Integer.parseInt(widthstr);
		}

		Integer height = null;
		if (heightstr != null) {
			height = Integer.parseInt(heightstr);
		}

		if (width != null && height != null) {
			return new java.awt.Dimension(width, height);
		} else if (width != null) {
			return new java.awt.Dimension(width, (int)super.getMaximumSize().getHeight());
		} else if (height != null) {
			return new java.awt.Dimension((int)super.getMaximumSize().getWidth(), height);
		} else {
			return super.getMaximumSize();
		}
	}

	public void appletResize(int width, int height) {}
}
