package org.nemac.geogaddi.options;

public class TransformationOption {
    private String name;
    private String transformationSourceLib;
    private String transformation;
    private String folder;
    private String file;
    private String normalDir;
    private Integer dateIndex;
    private Integer dataIndex;
    private String outName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransformationSourceLib() {
        return transformationSourceLib;
    }

    public void setTransformationSourceLib(String transformationSourceLib) {
        this.transformationSourceLib = transformationSourceLib;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getNormalDir() {
        return normalDir;
    }

    public void setNormalDir(String normalDir) {
        this.normalDir = normalDir;
    }

    public Integer getDateIndex() {
        return dateIndex;
    }

    public void setDateIndex(Integer dateIndex) {
        this.dateIndex = dateIndex;
    }

    public Integer getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(Integer dataIndex) {
        this.dataIndex = dataIndex;
    }

    public String getOutName() {
        return outName;
    }

    public void setOutName(String outName) {
        this.outName = outName;
    }
}
