package org.nemac.geogaddi.model;

import java.util.List;

public class DeriverOptions {
    private boolean enabled = true;
    private String sourceDir = "data/output";
    private List<TransformationOptions> transformations;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public List<TransformationOptions> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<TransformationOptions> transformations) {
        this.transformations = transformations;
    }
}
