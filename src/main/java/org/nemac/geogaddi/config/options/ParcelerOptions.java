package org.nemac.geogaddi.config.options;

import java.util.ArrayList;
import java.util.List;

public class ParcelerOptions {
    private Boolean enabled = true;
    private Boolean uncompress = true;
    private Boolean cleanSource = true;
    private Boolean cleanDestination = true;
//    private String whitelistFileSource;
    private List<String> sourceCSVs;
    private String folderWhiteList = "";
    private Integer folderWhiteListIndex = 0;
    private Integer folderIndex = 0;
    private String fileWhiteList;
    private Integer fileWhiteListIndex = 2;
    private Integer fileIndex = 2;
    private List<Integer> dataIndexes;
    private String outputDir = "data/output";

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isUncompress() {
        return uncompress;
    }

    public void setUncompress(Boolean uncompress) {
        this.uncompress = uncompress;
    }

    public Boolean isCleanSource() {
        return cleanSource;
    }

    public void setCleanSource(Boolean cleanSource) {
        this.cleanSource = cleanSource;
    }

    public Boolean isCleanDestination() {
        return cleanDestination;
    }

    public void setCleanDestination(Boolean cleanDestination) {
        this.cleanDestination = cleanDestination;
    }

    public List<String> getSourceCSVs() {
        if (sourceCSVs == null) {
            sourceCSVs = new ArrayList<>();
        }
        return sourceCSVs;
    }

    public void setSourceCSVs(List<String> sourceCSVs) {
        this.sourceCSVs = sourceCSVs;
    }

    public String getFolderWhiteList() {
        return folderWhiteList;
    }

    public void setFolderWhiteList(String folderWhiteList) {
        this.folderWhiteList = folderWhiteList;
    }

    public Integer getFolderWhiteListIndex() {
        return folderWhiteListIndex;
    }

    public void setFolderWhiteListIndex(Integer folderWhiteListIndex) {
        this.folderWhiteListIndex = folderWhiteListIndex;
    }

    public Integer getFolderIndex() {
        return folderIndex;
    }

    public void setFolderIndex(Integer folderIndex) {
        this.folderIndex = folderIndex;
    }

    public String getFileWhiteList() {
        return fileWhiteList;
    }

    public void setFileWhiteList(String fileWhiteList) {
        this.fileWhiteList = fileWhiteList;
    }

    public Integer getFileWhiteListIndex() {
        return fileWhiteListIndex;
    }

    public void setFileWhiteListIndex(Integer fileWhiteListIndex) {
        this.fileWhiteListIndex = fileWhiteListIndex;
    }

    public Integer getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(Integer fileIndex) {
        this.fileIndex = fileIndex;
    }

    public List<Integer> getDataIndexes() {
        if (dataIndexes == null) {
            dataIndexes = new ArrayList<>();
        }

        return dataIndexes;
    }

    public void setDataIndexes(List<Integer> dataIndexes) {
        this.dataIndexes = dataIndexes;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
}
