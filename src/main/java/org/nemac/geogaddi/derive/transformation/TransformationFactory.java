package org.nemac.geogaddi.derive.transformation;

public class TransformationFactory {
    
    private TransformationFactory() {
        //
    }
    
    // factory for registered transformations - it is recommended that we keep up with this
    public static AbstractTransformation createTransformation(final TransformationType type) {
        switch (type) {
            case YTDCumulative:
                return new YTDCumulative();
            default:
                return null;
        }
    }
    
    // factory for ad-hoc transformation classes - not recommended
    public static AbstractTransformation createTransformation(final String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class c = Class.forName(clazz);
        return (AbstractTransformation) c.newInstance();
    }
    
}
