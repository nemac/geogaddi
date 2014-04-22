package org.nemac.geogaddi.model;

public class IntegratorOptions {
    private Boolean enabled = false;
    private Boolean cleanSource = false;
    private String sourceDir = "data/output";
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String bucketName;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isCleanSource() {
        return cleanSource;
    }

    public void setCleanSource(Boolean cleanSource) {
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
