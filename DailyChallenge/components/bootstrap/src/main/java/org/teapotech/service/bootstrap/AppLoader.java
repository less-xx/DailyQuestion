/**
 * 
 */
package org.teapotech.service.bootstrap;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jiangl
 * 
 * This class is to use as a entry point of an application.
 * It will load the dependent lib files and invoke the given method of a specific class.
 */
public class AppLoader {
	private final List<URL> classpathList = new LinkedList<URL>();
	private Object daemon = null;
	private static File APPLICATION_HOME = null;

	private static boolean isAppHomeValid() {
		String sah = System.getProperty("APPLICATION_HOME");
		if (sah == null)
			return false;
		APPLICATION_HOME = new File(sah);
		return APPLICATION_HOME.exists();
	}

	private void addDefaultClassPaths() {
		File libPath = new File(APPLICATION_HOME, "lib");
		if (libPath.exists()) {
			File[] jars = libPath.listFiles();
			for (File jar : jars)
				addClassPath(jar);
		} else
			System.out.println("Library folder does not exist. "
			        + libPath.getAbsolutePath());
	}

	public void addClassPath(String path) {
		addClassPath(new File(path));
	}

	public void addClassPath(File path) {
		if (path.exists()) {
			try {
				if (path.isDirectory()) {
					File[] files = path.listFiles();
					for (File f : files)
						addClassPath(f);
				} else
					addClassPath(path.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			System.out.println(String.format("File not exists: %s",
			        path.getAbsolutePath()));
			System.exit(1);
		}
	}

	public void addClassPath(URL url) {
		classpathList.add(url);
	}

	private void invoke(String className, String method) throws Exception {
		ClassLoader tcl = Thread.currentThread().getContextClassLoader();
		Class<?> c = tcl.loadClass(className);
		Method m = c.getMethod(method, new Class[] {});
		daemon = c.newInstance();
		m.invoke(daemon, new Object[] {});
		System.out.println(String.format("Executed %s.%s()", c.getSimpleName(),
		        method));
	}

	private void initialClassLoader() {
		URLClassLoader loader = new URLClassLoader(
		        classpathList.toArray(new URL[] {}), getClass()
		                .getClassLoader());
		Thread.currentThread().setContextClassLoader(loader);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (!isAppHomeValid())
			throw new Error("APPLICATION_HOME is not valid.");
		if (args.length < 2) {
			System.out
			        .println(String
			                .format("Lack of parameters. <server_class_name}> and <server_class_method>"));
			System.exit(1);
		}
		AppLoader bootstrap = new AppLoader();
		try {
			bootstrap.addDefaultClassPaths();
			bootstrap.initialClassLoader();
			bootstrap.invoke(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}