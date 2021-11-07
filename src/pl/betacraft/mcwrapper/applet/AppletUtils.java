package pl.betacraft.mcwrapper.applet;

import java.applet.Applet;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import pl.betacraft.mcwrapper.BCWrapper;

public class AppletUtils {

	// Really ugly reflection stuff... But if Mojang themselves do it, who cares! lol
	public static boolean disableAppletMode(Applet applet, String minecraft_field_name, String appletMode_field_name) {
		try {
			Object minecraft = getMinecraft(applet, minecraft_field_name);

			Field appletMode_field = minecraft.getClass().getDeclaredField(appletMode_field_name);
			appletMode_field.setAccessible(true);
			appletMode_field.set(minecraft, false);
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}

	public static boolean setResolution(Applet applet, String minecraft_field_name, String width_field_name, String height_field_name, int width, int height) {
		try {
			Object minecraft = getMinecraft(applet, minecraft_field_name);

			Field width_field = minecraft.getClass().getDeclaredField(width_field_name);
			width_field.setAccessible(true);
			width_field.set(minecraft, width);

			Field height_field = minecraft.getClass().getDeclaredField(height_field_name);
			height_field.setAccessible(true);
			height_field.set(minecraft, height);
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}

	public static boolean setFullscreen(Applet applet, String minecraft_field_name, String fullscreen_field_name, String canvas_field_name, boolean fullscreen) {
		try {
			Object minecraft = getMinecraft(applet, minecraft_field_name);

			Field width_field = minecraft.getClass().getDeclaredField(fullscreen_field_name);
			width_field.setAccessible(true);
			width_field.set(minecraft, fullscreen);

			Field canvas_field = minecraft.getClass().getDeclaredField(canvas_field_name);
			canvas_field.setAccessible(true);
			canvas_field.set(minecraft, null);
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}

	public static Object getMinecraft(Applet applet, String minecraft_field_name) {
		try {
			Field minecraft_field = applet.getClass().getDeclaredField(minecraft_field_name);
			minecraft_field.setAccessible(true);

			Object minecraft = minecraft_field.get(applet);
			return minecraft;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public static boolean setServer(Applet applet, String minecraft_field_name, String server_field_name, String server_class_name, String server_constructor_arguments) {
		try {
			Object minecraft = getMinecraft(applet, minecraft_field_name);

			Object server = null;
			if (BCWrapper.arguments.get("server") != null) {
				Class<?> server_class = applet.getClass().getClassLoader().loadClass(server_class_name);
				Constructor<?> server_constructor = server_class.getConstructor(argumentsToClasses(server_constructor_arguments.split(","), minecraft));
				server_constructor.setAccessible(true);
				server = server_constructor.newInstance(argumentsToObjects(server_constructor_arguments.split(","), minecraft));
			}

			Field server_field = minecraft.getClass().getDeclaredField(server_field_name);
			server_field.setAccessible(true);
			server_field.set(minecraft, server);
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}

	public static Class<?>[] argumentsToClasses(String[] args, Object minecraft) {
		Class<?>[] returns = new Class[args.length];

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.equalsIgnoreCase("minecraft")) {
				returns[i] = minecraft.getClass();
			} else if (arg.equalsIgnoreCase("server") || arg.equalsIgnoreCase("username") || arg.equalsIgnoreCase("mppass") || arg.equalsIgnoreCase("sessionid")) {
				returns[i] = String.class;
			} else if (arg.equalsIgnoreCase("port")) {
				returns[i] = int.class;
			}
		}
		return returns;
	}

	public static Object[] argumentsToObjects(String[] args, Object minecraft) {
		Object[] returns = new Object[args.length];

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.equalsIgnoreCase("minecraft")) {
				returns[i] = minecraft;

				if (BCWrapper.debug)
				    System.out.println("Index " + i + ": " + Boolean.toString(minecraft != null));

			} else if (arg.equalsIgnoreCase("port")) {
				returns[i] = Integer.parseInt(BCWrapper.arguments.get("port"));

				if (BCWrapper.debug)
				    System.out.println("Index " + i + ": " + BCWrapper.arguments.get("port"));

			} else {
				returns[i] = BCWrapper.arguments.get(arg);

				if (BCWrapper.debug)
				    System.out.println("Index " + i + ": " + BCWrapper.arguments.get(arg));
			}
		}
		return returns;
	}
}
