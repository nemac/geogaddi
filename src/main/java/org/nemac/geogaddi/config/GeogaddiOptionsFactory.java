package org.nemac.geogaddi.config;

import org.nemac.geogaddi.model.GeogaddiOptions;

import java.io.IOException;

public class GeogaddiOptionsFactory {

    private GeogaddiOptionsFactory() {
        //
    }

    public static GeogaddiOptions instanceOf(final PropertyManagerTypeEnum managerType, final String propertiesSource) throws IOException {
        switch (managerType) {
            case JSON_PROPS:
                return new JSONPropertiesDeserializer(propertiesSource).deserialize();
            case JAVA_PROPS:
                //return new SimplePropertiesManager(propertiesSource);
            default:
                return null;
        }

    }
}
