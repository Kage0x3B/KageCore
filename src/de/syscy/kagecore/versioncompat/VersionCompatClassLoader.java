package de.syscy.kagecore.versioncompat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.versioncompat.reflect.Reflect;
import de.syscy.kagecore.versioncompat.reflect.ReflectException;
import lombok.Getter;

public class VersionCompatClassLoader {
	private static @Getter String serverVersion;

	private static Map<Class<?>, Object> compatibleClassCache;

	public static void init() {
		serverVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		compatibleClassCache = new HashMap<>();
	}

	/**
	 * Loads an instance of a class with the specified version for the current server version, used for compatibility with different server versions.<br><br>
	 * When searching for a class, the following locations are searched:
	 * <ul>
	 * <li><i>interfacePackage.interfaceClassName_serverVersion</i></li>
	 * <li><i>interfacePackage.serverVersion.interfaceClassName_serverVersion</i></li>
	 * <li><i>interfacePackage.versioncompat.interfaceClassName_serverVersion</i></li>
	 * <li><i>interfacePackage.interfaceClassName_Fallback</i></li>
	 * <li><i>interfacePackage.versioncompat.interfaceClassName_Fallback</i></li>
	 * </ul>
	 *
	 * Example:<br>
	 * The interface is in com.test.MyInterface and the version is v1_11_R1.<br>
	 * The version compatible instance or fallback class is then searched in these places:<br><br>
	 * <ul>
	 * <li><i>com.test.MyInterface_v1_11_R1</i></li>
	 * <li><i>com.test.v1_11_R1.MyInterface_v1_11_R1</i></li>
	 * <li><i>com.test.versioncompat.MyInterface_v1_11_R1</i></li>
	 * <li><i>com.test.MyInterface_Fallback</i></li>
	 * <li><i>com.test.versioncompat.MyInterface_Fallback</i></li>
	 * </ul>
	 *
	 * @param interfaceClass an interface class
	 * @param args arguments for the constructor of the version compatible class
	 * @return An instance of a class with the specified interface if a version compatible class was found, it returns a fallback class or null.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T loadClass(Class<T> interfaceClass, Object... args) {
		if(compatibleClassCache.containsKey(interfaceClass)) {
			return (T) compatibleClassCache.get(interfaceClass);
		}

		String interfaceClassPath = interfaceClass.getPackage() + "." + interfaceClass.getName();

		Reflect compatibleClass = null;

		//Try to load a version compatible class

		compatibleClass = loadClass(interfaceClassPath + "_" + serverVersion);

		if(compatibleClass == null) {
			compatibleClass = loadClass(interfaceClass.getPackage() + "." + serverVersion + "." + interfaceClass.getName());
		}

		if(compatibleClass == null) {
			compatibleClass = loadClass(interfaceClass.getPackage() + ".versioncompat." + interfaceClass.getName() + "_" + serverVersion);
		}

		//Try to find a fallback class

		if(compatibleClass == null) {
			compatibleClass = loadClass(interfaceClassPath + "_Fallback");
		}

		if(compatibleClass == null) {
			compatibleClass = loadClass(interfaceClass.getPackage() + ".versioncompat." + interfaceClass.getName() + "_Fallback");
		}

		if(compatibleClass == null) {
			KageCore.debugMessage("Could not find version compatible/fallback class for " + interfaceClass.getSimpleName());

			return null;
		}

		T instance = (T) compatibleClass.create(args);

		compatibleClassCache.put(interfaceClass, instance);

		return instance;
	}

	public static Reflect loadClass(String classPath) {
		try {
			return Reflect.on(classPath);
		} catch(ReflectException ex) {
			return null;
		}
	}
}