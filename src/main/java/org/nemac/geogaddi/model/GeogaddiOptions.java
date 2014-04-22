package org.nemac.geogaddi.model;

public class GeogaddiOptions {
    private static DeriverOptions deriverOptions;
    private static FetcherOptions fetcherOptions;
    private static IntegratorOptions integratorOptions;
    private static ParcelerOptions parcelerOptions;
    private static TransformationOption transformationOption;
    private static Boolean quiet = false;
    private static Boolean useAll = false;
    private static Boolean uncompress = false;

//    private GeogaddiOptions() {
//        instance.deriverOptions = new DeriverOptions();
//        instance.fetcherOptions = new FetcherOptions();
//        instance.integratorOptions = new IntegratorOptions();
//        instance.parcelerOptions = new ParcelerOptions();
//        instance.transformationOption = new TransformationOption();
//    }

    public GeogaddiOptions() {
        instance.deriverOptions = new DeriverOptions();
        instance.fetcherOptions = new FetcherOptions();
        instance.integratorOptions = new IntegratorOptions();
        instance.parcelerOptions = new ParcelerOptions();
        instance.transformationOption = new TransformationOption();
    }

    private static GeogaddiOptions instance;

    public static synchronized GeogaddiOptions getInstance(){
        if(instance == null){
            instance = new GeogaddiOptions();
        }
        return instance;
    }

    public static DeriverOptions getDeriverOptions() {
        return deriverOptions;
    }

    public static void setDeriverOptions(DeriverOptions deriverOptions) {
        instance.deriverOptions = deriverOptions;
    }

    public static FetcherOptions getFetcherOptions() {
        return fetcherOptions;
    }

    public static void setFetcherOptions(FetcherOptions fetcherOptions) {
        instance.fetcherOptions = fetcherOptions;
    }

    public static IntegratorOptions getIntegratorOptions() {
        return integratorOptions;
    }

    public static void setIntegratorOptions(IntegratorOptions integratorOptions) {
        instance.integratorOptions = integratorOptions;
    }

    public static ParcelerOptions getParcelerOptions() {
        return parcelerOptions;
    }

    public static void setParcelerOptions(ParcelerOptions parcelerOptions) {
        instance.parcelerOptions = parcelerOptions;
    }

    public static TransformationOption getTransformationOption() {
        return transformationOption;
    }

    public static void setTransformationOption(TransformationOption transformationOption) {
        instance.transformationOption = transformationOption;
    }

    public static Boolean isQuiet() {
        return quiet;
    }

    public static void setQuiet(Boolean quiet) {
        instance.quiet = quiet;
    }

    public static Boolean isUseAll() {
        return useAll;
    }

    public static void setUseAll(Boolean useAll) {
        instance.useAll = useAll;
    }

    public static Boolean isUncompress() {
        return uncompress;
    }

    public static void setUncompress(Boolean uncompress) {
        instance.uncompress = uncompress;
    }
}
