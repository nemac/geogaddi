package org.nemac.geogaddi.options;

import java.util.ArrayList;
import java.util.List;

public class DeriverOptions {
    private Boolean enabled = true;
    private String sourceDir = "data/output";
    private List<TransformationOption> transformationOptions;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public List<TransformationOption> getTransformationOptions() {
        if (transformationOptions == null) {
            transformationOptions = new ArrayList<>();
        }
        return transformationOptions;
    }

    public void setTransformationOptions(List<TransformationOption> transformationOptions) {
        this.transformationOptions = transformationOptions;
    }
}
