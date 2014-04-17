package org.nemac.geogaddi.config.element;

public class IntegratorOptions {
    private boolean enabled = false;
    private boolean cleanSource = false;
    private String sourceDir = "data/output";
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String bucketName;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCleanSource() {
        return cleanSource;
    }

    public void setCleanSource(boolean cleanSource) {
        this.cleanSource = cleanSource;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
