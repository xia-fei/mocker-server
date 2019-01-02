package org.wing.mocker.http.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

public class JarService {
    private final Logger LOGGER= LoggerFactory.getLogger(JarService.class);
    private String jarHttpUrl;

    /**
     * 形如
     * jar包的http地址
     */
    public JarService(String jarHttpUrl) {
        this.jarHttpUrl = jarHttpUrl;
    }

    public List<Class> getClassList() throws IOException{
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarHttpUrl)});
        URL jarURL = new URL("jar:" + jarHttpUrl + "!/");
        JarURLConnection jarURLConnection = (JarURLConnection) jarURL.openConnection();
        Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
        List<Class> classList = new ArrayList<>();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarEntryName = jarEntry.getName();
            if (jarEntryName.endsWith(".class")) {
                Class jarClass;
                try {
                    String className=jarEntryName.replace("/", ".").substring(0, jarEntryName.length()-6);
                    jarClass = urlClassLoader.loadClass(className);
                    if (isBeanClass(jarClass)) {
                        classList.add(jarClass);
                    }
                } catch (ClassNotFoundException|NoClassDefFoundError e) {
                    LOGGER.info("获取jarEntry Class失败 {}",jarEntryName);
                }

            }
        }
        return classList;
    }

    private boolean isBeanClass(Class jarClass) {
        return !jarClass.isEnum() && !jarClass.isInterface() && !jarClass.isAnnotation();
    }

}
