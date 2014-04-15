package org.nemac.geogaddi.derive.transformation;

public enum TransformationType {
    
    YTDCumulative("YTD_CUMULATIVE");
    
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
        
        return getDefault();
    }
    
    public static TransformationType getDefault() {
        return YTDCumulative;
    }
    
}
