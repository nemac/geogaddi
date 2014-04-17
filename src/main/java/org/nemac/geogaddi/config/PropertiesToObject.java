package org.nemac.geogaddi.config;

import org.nemac.geogaddi.config.element.ParcelerOptions;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class PropertiesToObject {
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";
    private static final String PROPERTIES_PACKAGE = "org.nemac.geogaddi.config.element";

    final String propertiesSource;

    public PropertiesToObject(String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    public void deserialize() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        FileReader reader = new FileReader(propertiesSource);
        Properties props = new Properties();

        props.load(reader);

        Enumeration e = props.propertyNames();
        while (e.hasMoreElements()) {
            String element = (String) e.nextElement();
            String[] splitElements = element.split("\\.");

            if ("parceler".equals(splitElements[0])) {
                Class<?> clazz = Class.forName(PROPERTIES_PACKAGE + "." + capitalizedOptionType(splitElements[0]) + "Options");
                clazz.newInstance();
            }
        }

        System.out.println(props.get("zzz"));
    }

    private String capitalizedOptionType(final String orig) {
        return orig.substring(0, 1).toUpperCase() + orig.substring(1);
    }

    private static List<Method> findGettersSetters(Class<?> c) {
        ArrayList<Method> list = new ArrayList<Method>();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods)
            if (isSetter(method)) {
                list.add(method);
            }
        return list;
    }

    public static boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                method.getReturnType().equals(void.class) &&
                method.getParameterTypes().length == 1 &&
                method.getName().matches("^set[A-Z].*");
    }
}
