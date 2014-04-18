package org.nemac.geogaddi.model;

import java.util.Arrays;
import java.util.List;

public class ParcelerOptions {
    private boolean enabled = true;
    private boolean uncompress = false;
    private boolean cleanSource = false;
    private boolean cleanDestination = true;
    private boolean existingFromIntegrator = true;
//    private String whitelistFileSource;
    private List<String> sourceCSVs;
    private String folderWhiteList = "";
    private int folderWhiteListIndex = 0;
    private int folderIndex = 0;
    private String fileWhiteList;
    private int fileWhiteListIndex = 2;
    private int fileIndex = 2;
    private List<Integer> dataIndexes = Arrays.asList(1, 3);
    private String outputDir = "data/output";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUncompress() {
        return uncompress;
    }

    public void setUncompress(boolean uncompress) {
        this.uncompress = uncompress;
    }

    public boolean isCleanSource() {
        return cleanSource;
    }

    public void setCleanSource(boolean cleanSource) {
        this.cleanSource = cleanSource;
    }

    public boolean isCleanDestination() {
        return cleanDestination;
    }

    public void setCleanDestination(boolean cleanDestination) {
        this.cleanDestination = cleanDestination;
    }

    public boolean isExistingFromIntegrator() {
        return existingFromIntegrator;
    }

    public void setExistingFromIntegrator(boolean existingFromIntegrator) {
        this.existingFromIntegrator = existingFromIntegrator;
    }

    public List<String> getSourceCSVs() {
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

    public int getFolderWhiteListIndex() {
        return folderWhiteListIndex;
    }

    public void setFolderWhiteListIndex(int folderWhiteListIndex) {
        this.folderWhiteListIndex = folderWhiteListIndex;
    }

    public int getFolderIndex() {
        return folderIndex;
    }

    public void setFolderIndex(int folderIndex) {
        this.folderIndex = folderIndex;
    }

    public String getFileWhiteList() {
        return fileWhiteList;
    }

    public void setFileWhiteList(String fileWhiteList) {
        this.fileWhiteList = fileWhiteList;
    }

    public int getFileWhiteListIndex() {
        return fileWhiteListIndex;
    }

    public void setFileWhiteListIndex(int fileWhiteListIndex) {
        this.fileWhiteListIndex = fileWhiteListIndex;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public List<Integer> getDataIndexes() {
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
