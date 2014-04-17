package org.nemac.geogaddi.config.element;

import java.util.List;

public class FetcherOptions {
    private boolean enabled;
    private boolean uncompress;
    private List<String> sources;
    private String dumpDir;

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

    public List<String> getSources() {
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
