package org.nemac.geogaddi.config.element;

public class GeogaddiOptions {
    private DeriverOptions deriverOptions;
    private FetcherOptions fetcherOptions;
    private GeogaddiOptions geogaddiOptions;
    private IntegratorOptions integratorOptions;
    private ParcelerOptions parcelerOptions;
    private TransformationProperty transformationProperty;
    private boolean quiet = true;
    private boolean useAll = false;
    private boolean uncompress = false;

    public DeriverOptions getDeriverOptions() {
        return deriverOptions;
    }

    public void setDeriverOptions(DeriverOptions deriverOptions) {
        this.deriverOptions = deriverOptions;
    }

    public FetcherOptions getFetcherOptions() {
        return fetcherOptions;
    }

    public void setFetcherOptions(FetcherOptions fetcherOptions) {
        this.fetcherOptions = fetcherOptions;
    }

    public GeogaddiOptions getGeogaddiOptions() {
        return geogaddiOptions;
    }

    public void setGeogaddiOptions(GeogaddiOptions geogaddiOptions) {
        this.geogaddiOptions = geogaddiOptions;
    }

    public IntegratorOptions getIntegratorOptions() {
        return integratorOptions;
    }

    public void setIntegratorOptions(IntegratorOptions integratorOptions) {
        this.integratorOptions = integratorOptions;
    }

    public ParcelerOptions getParcelerOptions() {
        return parcelerOptions;
    }

    public void setParcelerOptions(ParcelerOptions parcelerOptions) {
        this.parcelerOptions = parcelerOptions;
    }

    public TransformationProperty getTransformationProperty() {
        return transformationProperty;
    }

    public void setTransformationProperty(TransformationProperty transformationProperty) {
        this.transformationProperty = transformationProperty;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isUseAll() {
        return useAll;
    }

    public void setUseAll(boolean useAll) {
        this.useAll = useAll;
    }

    public boolean isUncompress() {
        return uncompress;
    }

    public void setUncompress(boolean uncompress) {
        this.uncompress = uncompress;
    }
}
