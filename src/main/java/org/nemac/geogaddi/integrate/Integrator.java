package org.nemac.geogaddi.integrate;

import com.amazonaws.AmazonClientException;
import java.io.File;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.TransferManager;

public class Integrator {

    public static void integrate(AWSCredentials credentials, String sourceDir, String bucketName, boolean clean) throws AmazonClientException, InterruptedException {

        System.out.println("Transferring content to S3");

        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1)); // TODO: parameterize?

        if (!s3.doesBucketExist(bucketName)) {
            s3.createBucket(bucketName);
        }

        if (clean) {
            BucketDestroy.emptyBucket(s3, bucketName);
        }

        ObjectMetadataProvider metadataProvider = new ObjectMetadataProvider() {
            @Override
            public void provideObjectMetadata(File file, ObjectMetadata metadata) {
                metadata.setHeader("content-encoding", "gzip");
            }
        };

        TransferManager transfer = new TransferManager(s3);
        // TODO: after write GZ to S3, add metadataProvider as last uploadDirectory arg
        MultipleFileUpload upload  = transfer.uploadDirectory(bucketName, null, new File(sourceDir), true);
        upload.addProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                System.out.println("... transferred bytes: " + progressEvent.getBytesTransferred());
            }
        });
        
        upload.waitForCompletion();
        
        transfer.shutdownNow();

        System.out.println("... content transferred to S3");
    }
}
