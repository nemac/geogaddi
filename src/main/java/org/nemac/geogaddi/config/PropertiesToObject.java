package org.nemac.geogaddi.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.nemac.geogaddi.config.options.GeogaddiOptions;
import org.nemac.geogaddi.exception.PropertiesParseException;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PropertiesToObject {
/*    
    
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";

    private final String propertiesSource;

    public PropertiesToObject(String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    public GeogaddiOptions deserialize() throws PropertiesParseException {
        Configuration config = null;
        GeogaddiOptions geogaddiOptions = new GeogaddiOptions();

        try {
            config = new PropertiesConfiguration(propertiesSource);

            Iterator<String> geogaddiOptionKeys = config.getKeys("geogaddiOptions");
            parseAndSetOptionsFor(config, geogaddiOptions, geogaddiOptionKeys);

            Iterator<String> fetcherOptionKeys = config.getKeys("fetcherOptions");
            parseAndSetOptionsFor(config, geogaddiOptions.getFetcherOptions(), fetcherOptionKeys);

            Iterator<String> parcelerOptionKeys = config.getKeys("parcelerOptions");
            parseAndSetOptionsFor(config, geogaddiOptions.getParcelerOptions(), parcelerOptionKeys);

            Iterator<String> integratorOptionKeys = config.getKeys("integratorOptions");
            parseAndSetOptionsFor(config, geogaddiOptions.getIntegratorOptions(), integratorOptionKeys);

            Iterator<String> deriverOptionKeys = config.getKeys("deriverOptions");
            parseAndSetOptionsFor(config, geogaddiOptions.getDeriverOptions(), deriverOptionKeys);

            // TODO: implement more than one transformation for java-style properties?
            if (!geogaddiOptions.getDeriverOptions().getTransformationOptions().isEmpty()) {
                Iterator<String> transformationOptionKeys = config.getKeys("deriverOptions.transformationOptions");
                parseAndSetOptionsFor(config, geogaddiOptions.getDeriverOptions().getTransformationOptions().get(0), transformationOptionKeys);
            }
        } catch (ConfigurationException | NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new PropertiesParseException("Failed to parse properties file: " + propertiesSource, e);
        }

        return geogaddiOptions;
    }

    private <T> void parseAndSetOptionsFor(Configuration config, T t, Iterator<String> optionKeys)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Method[] allMethods = t.getClass().getDeclaredMethods();

        while (optionKeys.hasNext()) {
            String key = optionKeys.next();
            String[] optionValuesToSet = key.split("\\.");
            List<Object> values = config.getList(key);

            for (String optionValueToSet : optionValuesToSet) {
                String setterMethodName = "set" + optionValueToSet;

                // find the setter method
                for (Method method : allMethods) {
                    if (setterMethodName.toLowerCase().equals(method.getName().toLowerCase())) {
                        Type parameterType = method.getParameterTypes()[0];

                        if (Class.forName(parameterType.getTypeName()).equals(List.class)) {
                            // find the generic type of the field
                            Type parameterGenericType = getGenericTypeFor(optionValueToSet, t.getClass());

                            // create array of generic type
                            Object[] objects = createTypedObjectArrayFrom(values, parameterGenericType);

                            // set
                            method.invoke(t, Arrays.asList(objects));
                        } else {
                            // find the constructor for the Type and invoke
                            Constructor<?> declaredConstructor = declaredConstructorFor(method);
                            method.invoke(t, declaredConstructor.newInstance(values.get(0)));
                        }
                    }
                }
            }

        }
    }

    @SuppressWarnings("unchecked")
    private <T> T[] createTypedObjectArrayFrom(List<Object> values, T t) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] objects = new Object[values.size()];

        Constructor typeConstructor;
        Constructor<?>[] constructors = Class.forName(((Class) t).getTypeName()).getConstructors();
        Constructor constructor = getStringParameterTypeConstructor(constructors);

        boolean constructorHasArgs = constructor.getParameterCount() > 0;

        for (int i = 0; i < values.size(); i++) {
            if (constructorHasArgs) {
                objects[i] = constructor.newInstance(values.get(i));
            } else {
                objects[i] = constructor.newInstance();
            }
        }

        return (T[]) objects;
    }

    private Constructor getStringParameterTypeConstructor(Constructor<?>[] constructors) {
        Constructor typeConstructor = null;
        Constructor emptyConstructor = null;

        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                emptyConstructor = constructor;
            }

            for (Class<?> parameterClass : constructor.getParameterTypes()) {
                if (parameterClass.isAssignableFrom(String.class)) {
                    typeConstructor = constructor;
                    break;
                }
            }
        }

        return typeConstructor != null ? typeConstructor : emptyConstructor;
    }

    private Type getGenericTypeFor(String optionValueToSet, Class<?> clazz) throws NoSuchFieldException {
        Field declaredField = clazz.getDeclaredField(optionValueToSet);
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
*/
}