package org.smart4j.framework.util;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类操作工具类
 * 
 * @author shuang
 * @version 1.0.0
 */
public final class ClassUtil {
	/**
	 * 获取类加载器
	 * 获取当前线程中的ClassLoader 
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	/**
	 * 加载类，获得Class的实例
	 * 任何对象都是Class类的实例
	 * 
	 * @param className 类名
	 * @param isInitialized 是否初始化  是否执行类的静态代码块,为了提高加载类的性能，
	 * 						可将loadClass方法的isInitialized的参数设置为false
	 * @return
	 */
	public static Class<?> loadClass(String className, boolean isInitialized) {
		Class<?> cls;
		try {
			cls = Class.forName(className, isInitialized, getClassLoader()); // 使用给定的类加载器加载类并返回Class对象
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return cls;
	}
	/**
	 * 获取指定包名下的所有的类
	 * 根据包名将其转换为文件路径，读取class文件或jar包，获取指定的类去加载类
	 * @param packageName
	 * @return
	 */
	public static Set<Class<?>> getClassSet(String packageName) {
		// 存放Class类的Set集合
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		try {
			// getResource(String name) 查找具有给定名称的资源
			// Enumeration 接口的对象，它生成一系列元素，一次生成一个。连续调用 nextElement 方法将返回一系列的连续元素
			Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));  
			while (urls.hasMoreElements()) { // 判断此枚举是否包含更多的元素
				URL url = urls.nextElement(); // 返回此枚举的下一个元素
				if (url != null) {
					String protocol = url.getProtocol(); // 获取此 URL的协议名称
					if(protocol.equals("file")) {
						String packagePath = url.getPath().replaceAll("%20", ""); // getPath() 获取此 URL 的路径部分
						addClass(classSet, packagePath, packageName);
					} else if (protocol.equals("jar")) {
						JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
						if (jarURLConnection != null) {
							JarFile jarFile = jarURLConnection.getJarFile();
							if (jarFile != null) {
								Enumeration<JarEntry> jarEntries = jarFile.entries();
								while (jarEntries.hasMoreElements()) {
									JarEntry jarEntry = jarEntries.nextElement();
									String jarEntryName = jarEntry.getName();
									if (jarEntryName.endsWith(".class")) {
										String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
										doAddClass(classSet, className);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classSet;
	}
	/**
	 * 递归遍历类或文件，判断是否是.class文件或者标准文件
	 * @param classSet
	 * @param packagePath
	 * @param packageName
	 */
	public static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
		File[] files = new File(packagePath).listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
		for (File file : files) {
			String fileName = file.getName();
			if (file.isFile()) { // 此抽象路径名表示的文件是否是一个标准文件
				String className = fileName.substring(0, fileName.lastIndexOf("."));
				if (StringUtil.isNotEmpty(packageName)) {
					className = packageName + "." + className;
				    }
				doAddClass(classSet, className);
			}else {
			      	String subPackagePath = fileName;
				if (StringUtil.isNotEmpty(packagePath)) {
					subPackagePath = packagePath + "/" + subPackagePath;
				}
				String subPackageName = fileName;
				if (StringUtil.isNotEmpty(packageName)) {
					subPackageName = packageName + "." +subPackageName;
				}
				addClass(classSet, subPackagePath, subPackageName);
			}
		}
	}
	/**
	 * 将类放入 Set<Class<?>> 中
	 * @param classSet 存放Class类的Set集合
	 * @param className 类名
	 */
	public static void doAddClass(Set<Class<?>> classSet, String className) {
		Class<?> cls = loadClass(className, false);
		classSet.add(cls);
	}
}
