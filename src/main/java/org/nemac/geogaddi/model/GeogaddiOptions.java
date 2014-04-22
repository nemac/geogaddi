package org.nemac.geogaddi.model;

public class GeogaddiOptions {
    private DeriverOptions deriverOptions;
    private FetcherOptions fetcherOptions;
    private IntegratorOptions integratorOptions;
    private ParcelerOptions parcelerOptions;
    private TransformationOption transformationOption;
    private Boolean quiet = false;
    private Boolean useAll = false;
    private Boolean uncompress = false;

    public GeogaddiOptions() {
        this.deriverOptions = new DeriverOptions();
        this.fetcherOptions = new FetcherOptions();
        this.integratorOptions = new IntegratorOptions();
        this.parcelerOptions = new ParcelerOptions();
        this.transformationOption = new TransformationOption();
    }

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

    public TransformationOption getTransformationOption() {
        return transformationOption;
    }

    public void setTransformationOption(TransformationOption transformationOption) {
        this.transformationOption = transformationOption;
    }

    public Boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(Boolean quiet) {
        this.quiet = quiet;
    }

    public Boolean isUseAll() {
        return useAll;
    }

    public void setUseAll(Boolean useAll) {
        this.useAll = useAll;
    }

    public Boolean isUncompress() {
        return uncompress;
    }

    public void setUncompress(Boolean uncompress) {
        this.uncompress = uncompress;
    }
}
