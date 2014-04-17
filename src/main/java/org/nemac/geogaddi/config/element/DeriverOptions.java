package org.nemac.geogaddi.config.element;

import java.util.List;

public class DeriverOptions {
    private boolean enabled = true;
    private String sourceDir = "data/output";
    private List<TransformationProperty> transformations;

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

    public List<TransformationProperty> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<TransformationProperty> transformations) {
        this.transformations = transformations;
    }
}
