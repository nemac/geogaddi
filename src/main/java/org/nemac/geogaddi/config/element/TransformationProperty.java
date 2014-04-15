package org.nemac.geogaddi.config.element;

public class TransformationProperty {
    
    private final String name;
    private final String transformationSourceLib;
    private final String transformation;
    private final String folder;
    private final String file;
    private final String normalDir;
    private final int dateIndex;
    private final int dataIndex;
    private final String outName;

    public TransformationProperty(String name, String transformationSourceLib, String transformation, String folder, String file, String normalSource, int dateIndex, int dataIndex, String outName) {
        this.name = name;
        this.transformationSourceLib = transformationSourceLib;
        this.transformation = transformation;
        this.folder = folder;
        this.file = file;
        this.normalDir = normalSource;
        this.dateIndex = dateIndex;
        this.dataIndex = dataIndex;
        this.outName = outName;
    }
        
    public String getName() {
        return name;
    }
    
    public String getTransformationSourceLib() {
        return transformationSourceLib;
    }
    
    public String getTransformation() {
        return transformation;
    }

    public String getFolder() {
        return folder;
    }

    public String getFile() {
        return file;
    }

    public String getNormalDir() {
        return normalDir;
    }

    public int getDateIndex() {
        return dateIndex;
    }

    public int getDataIndex() {
        return dataIndex;
    }
    
    public String getOutName() {
        return outName;
    }
}
