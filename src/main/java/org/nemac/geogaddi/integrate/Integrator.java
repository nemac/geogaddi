package org.nemac.geogaddi.integrate;

import java.io.File;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;

public class Integrator {

	public static void integrate(AWSCredentials credentials, String sourceDir, String bucketName, boolean clean) {
		
		System.out.println("Transferring content to S3");
		
		AmazonS3 s3 = new AmazonS3Client(credentials);
		s3.setRegion(Region.getRegion(Regions.US_EAST_1)); // TODO: parameterize?
		
		if (!s3.doesBucketExist(bucketName)) {
			s3.createBucket(bucketName);
		}
		
		if (clean) {
			BucketDestroy.destroyBucket(s3, bucketName);
		}
		
		TransferManager transfer = new TransferManager(s3);
		transfer.uploadDirectory(bucketName, null, new File(sourceDir), true).addProgressListener(new ProgressListener() {
			@Override
			public void progressChanged(ProgressEvent progressEvent) {
				System.out.println("... transferred bytes: " + progressEvent.getBytesTransferred());
			}
		});
		
		System.out.println("... content transferred to S3");
	}
}
