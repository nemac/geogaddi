package org.nemac.geogaddi.derive.transformation;

public enum TransformationType {
    
    YTDCumulative("YTDCumulative");
    
    private final String type;
    
    private TransformationType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public static TransformationType fromType(final String type) {
        for (TransformationType transformerType : values()) {
            if (transformerType.type.equals(type)) {
                return transformerType;
            }
        }

        // make this null so the factory can try loading a Transformation from the classpath
        return null;
    }
    
    public static TransformationType getDefault() {
        return YTDCumulative;
    }
    
}
