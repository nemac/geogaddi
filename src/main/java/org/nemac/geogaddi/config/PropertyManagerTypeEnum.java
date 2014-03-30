package org.nemac.geogaddi.config;

public enum PropertyManagerTypeEnum {
    SIMPLE("p"), JSON("j");

    private final String arg;

    private PropertyManagerTypeEnum(final String arg) {
         this.arg = arg;
    }
    
    public String getArgValue() {
        return arg;
    }

}
