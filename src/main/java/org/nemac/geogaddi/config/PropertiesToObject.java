package org.nemac.geogaddi.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.nemac.geogaddi.model.GeogaddiOptions;
import org.nemac.geogaddi.model.ParcelerOptions;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

public class PropertiesToObject {
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";
    private static final String PROPERTIES_PACKAGE = "org.nemac.geogaddi.model";

    final String propertiesSource;

    public PropertiesToObject(String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    public GeogaddiOptions deserialize() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, ConfigurationException, NoSuchMethodException, NoSuchFieldException {
        FileReader reader = new FileReader(propertiesSource);
        Properties props = new Properties();

        props.load(reader);

        Enumeration e = props.propertyNames();

        Configuration config = new PropertiesConfiguration(propertiesSource);

        GeogaddiOptions geogaddiOptions = new GeogaddiOptions();
        ParcelerOptions parcelerOptions = geogaddiOptions.getParcelerOptions();

//        Iterator<String> fetcherOptions = config.getKeys("fetcherOptions");
//        Iterator<String> integratorOptions = config.getKeys("integratorOptions");
//        Iterator<String> deriverOptions = config.getKeys("deriverOptions");
        // todo
//        Iterator<String> transformations = config.getKeys("transformations");

        Iterator<String> parcelerOptionKeys = config.getKeys("parcelerOptions");
        Method[] allMethods = parcelerOptions.getClass().getDeclaredMethods();

        while (parcelerOptionKeys.hasNext()) {
            String key = parcelerOptionKeys.next();
            String optionValueToSet = key.split("\\.")[1];
            List<Object> values = config.getList(key);

            String setterMethodName = "set" + optionValueToSet;

            // find the setter method
            for (Method method : allMethods) {
                if (setterMethodName.toLowerCase().equals(method.getName().toLowerCase())) {
                    Type parameterType = method.getParameterTypes()[0];

                    if (Class.forName(parameterType.getTypeName()).equals(List.class)) {
                        // find the generic type of the field
                        Type parameterGenericType = getGenericTypeFor(optionValueToSet);

                        Object[] objects = createTypedObjectArrayFrom(values, parameterGenericType);
                        method.invoke(parcelerOptions, Arrays.asList(objects));
                    } else {
                        // find the constructor for the Type and invoke
                        Constructor<?> declaredConstructor = declaredConstructorFor(method);
                        method.invoke(parcelerOptions, declaredConstructor.newInstance(values.get(0)));
                    }
                }
            }
        }

        return geogaddiOptions;
    }

    @SuppressWarnings("unchecked")
    private <T> T[] createTypedObjectArrayFrom(List<Object> values, T parameterGenericTypeClass) {
        Object[] objects = new Object[values.size()];
        for (int i = 0; i < values.size(); i++) {
            try {
                objects[i] = Class.forName(((Class) parameterGenericTypeClass).getTypeName()).getConstructor(String.class).newInstance(values.get(i));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                // todo: better error handling
                e.printStackTrace();
            }
        }

        return (T[]) objects;
    }

    private Type getGenericTypeFor(String optionValueToSet) throws NoSuchFieldException {
        Field declaredField = ParcelerOptions.class.getDeclaredField(optionValueToSet);
        return ((ParameterizedTypeImpl) declaredField.getGenericType()).getActualTypeArguments()[0];
    }

    private Constructor<?> declaredConstructorFor(Method method) throws ClassNotFoundException, NoSuchMethodException {
        // get Type of the first parameter (assumes one parameter for a setter method)
        Type parameterType = method.getParameterTypes()[0];

        Class<?> aClass = Class.forName(parameterType.getTypeName());

        if (aClass.equals(List.class)) {
            return Arrays.asList().getClass().getDeclaredConstructors()[0];
        } else {
            // create constructor for the parameterType
            return aClass.getDeclaredConstructor(String.class);
        }
    }

}