package org.nemac.geogaddi.config;

import org.nemac.geogaddi.exception.PropertiesParseException;
import org.nemac.geogaddi.config.options.GeogaddiOptions;

public class GeogaddiOptionsFactory {

    private GeogaddiOptionsFactory() {
        //
    }

    public static GeogaddiOptions instanceOf(final PropertyTypes managerType, final String propertiesSource) throws PropertiesParseException {
        switch (managerType) {
            case JSON_PROPS:
                return new JSONPropertiesDeserializer(propertiesSource).deserialize();
            case JAVA_PROPS:
                return new PropertiesToObject(propertiesSource).deserialize();
            default:
                return null;
        }

    }
}
