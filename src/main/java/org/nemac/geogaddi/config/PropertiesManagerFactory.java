package org.nemac.geogaddi.config;

public final class PropertiesManagerFactory {

    private PropertiesManagerFactory() {
        //
    }

    public static PropertiesManager createPropertyManager(final PropertyManagerTypeEnum managerType, final String propertiesSource) {
        switch (managerType) {
            case JSON:
                return new JSONPropertiesManager(propertiesSource);
            case SIMPLE:
                return new SimplePropertiesManager(propertiesSource);
            default:
                return null;
        }
    }
}
