package org.nemac.geogaddi.config.options;

import java.util.ArrayList;
import java.util.List;

public class FetcherOptions {
    private Boolean enabled = true;
    private Boolean uncompress = true;
    private List<String> sources;
    private String dumpDir = "data/dump";

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

    public List<String> getSources() {
        if (sources == null) {
            sources = new ArrayList<>();
        }
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public String getDumpDir() {
        return dumpDir;
    }

    public void setDumpDir(String dumpDir) {
        this.dumpDir = dumpDir;
    }
}
