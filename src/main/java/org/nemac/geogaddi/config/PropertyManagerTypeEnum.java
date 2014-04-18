package org.nemac.geogaddi.config;

public enum PropertyManagerTypeEnum {
    JAVA_PROPS("p"), JSON_PROPS("j");

    private final String arg;

    private PropertyManagerTypeEnum(final String arg) {
         this.arg = arg;
    }
    
    public String getArgValue() {
        return arg;
    }

}
