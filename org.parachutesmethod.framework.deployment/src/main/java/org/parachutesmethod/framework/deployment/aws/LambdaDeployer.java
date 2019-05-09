package org.parachutesmethod.framework.deployment.aws;

import java.io.IOException;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

public class LambdaDeployer {

    public static void puloadLambdaPackagesToS3Bucket(String tempProjectDirName) {
        try {
            String bucketName = "tempProjectDirName" + System.currentTimeMillis();
            ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
            //credentialsProvider.

            S3Client s3Client = S3Client.builder().credentialsProvider(ProfileCredentialsProvider.create()).build();

            if (!s3Client.  doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Bucket location: " + bucketLocation);
            }
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }


}
