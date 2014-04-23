package org.nemac.geogaddi.config;

public enum PropertyTypes {
    JAVA_PROPS("p"), JSON_PROPS("j");

    private final String arg;

    private PropertyTypes(final String arg) {
         this.arg = arg;
    }
    
    public String getArgValue() {
        return arg;
    }

}
