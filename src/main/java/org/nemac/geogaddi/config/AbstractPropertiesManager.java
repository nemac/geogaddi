package org.nemac.geogaddi.config;

import com.amazonaws.auth.BasicAWSCredentials;
import java.util.List;

public abstract class AbstractPropertiesManager implements PropertiesManager {
    protected boolean override;
    protected boolean quiet;
    protected boolean useAll;
    protected boolean fetcherEnabled;
    protected boolean fetcherUncompress;
    protected List<String> fetcherUrls;
    protected String fetcherDumpDir;
    protected boolean parcelerEnabled;
    protected boolean parcelerUncompress;
    protected boolean parcelerCleanSource;
    protected boolean parcelerCleanDestination;
    protected boolean parcelerExistingFromIntegrator;
    protected List<String> parcelerSources;
    protected String parcelerDestinatonDir;
    protected String parcelerFolderWhiteListSource;
    protected int parcelerFolderWhiteListIdx;
    protected int parcelerFolderIdx;
    protected String parcelerFileWhiteListSource;
    protected int parcelerFileWhiteListIdx;
    protected int parcelerFileIdx;
    protected int[] parcelerDataIdxArr;
    protected boolean integratorEnabled;
    protected boolean integratorCleanSource;
    protected String integratorSourceDir;
    protected BasicAWSCredentials integratorCredentials;
    protected String integratorBucketName;
    protected String propertiesSource;

    @Override
    public boolean isOverride() {
        return override;
    }
    
    @Override
    public boolean isQuiet() {
        return quiet;
    }

    @Override
    public boolean isUseAll() {
        return useAll;
    }

    @Override
    public boolean isFetcherEnabled() {
        return fetcherEnabled;
    }

    @Override
    public boolean isFetcherUncompress() {
        return fetcherUncompress;
    }

    @Override
    public List<String> getFetchUrls() {
        return fetcherUrls;
    }

    @Override
    public String getDumpDir() {
        return fetcherDumpDir;
    }

    @Override
    public boolean isParcelerEnabled() {
        return parcelerEnabled;
    }

    @Override
    public boolean isParcelerUncompress() {
        return parcelerUncompress;
    }

    @Override
    public boolean isParcelerCleanSource() {
        return parcelerCleanSource;
    }

    @Override
    public boolean isParcelerCleanDestination() {
        return parcelerCleanDestination;
    }

    @Override
    public boolean isParcelerExistingFromIntegrator() {
        return parcelerExistingFromIntegrator;
    }

    @Override
    public List<String> getParcelerSources() {
        return parcelerSources;
    }

    @Override
    public String getDestinatonDir() {
        return parcelerDestinatonDir;
    }

    @Override
    public String getFolderWhiteListSource() {
        return parcelerFolderWhiteListSource;
    }

    @Override
    public int getFolderWhiteListIdx() {
        return parcelerFolderWhiteListIdx;
    }

    @Override
    public int getFolderIdx() {
        return parcelerFolderIdx;
    }

    @Override
    public String getFileWhiteListSource() {
        return parcelerFileWhiteListSource;
    }

    @Override
    public int getFileWhiteListIdx() {
        return parcelerFileWhiteListIdx;
    }

    @Override
    public int getFileIdx() {
        return parcelerFileIdx;
    }

    @Override
    public int[] getDataIdxArr() {
        return parcelerDataIdxArr;
    }

    @Override
    public boolean isIntegratorEnabled() {
        return integratorEnabled;
    }

    @Override
    public boolean isIntegratorCleanSource() {
        return integratorCleanSource;
    }

    @Override
    public String getIntegratorSourceDir() {
        return integratorSourceDir;
    }

    @Override
    public BasicAWSCredentials getCredentials() {
        return integratorCredentials;
    }

    @Override
    public String getBucketName() {
        return integratorBucketName;
    }
}
