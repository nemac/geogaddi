package org.nemac.geogaddi.model;

public class TransformationOptions {
    private String name;
    private String transformationSourceLib;
    private String transformation;
    private String folder;
    private String file;
    private String normalDir;
    private int dateIndex;
    private int dataIndex;
    private String outName;

    public TransformationOptions() {

    }

    public TransformationOptions(String name, String transformationSourceLib, String transformation, String folder, String file, String normalSource, int dateIndex, int dataIndex, String outName) {
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
