package org.parachutesmethod.framework.deployment.aws;

import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;
import java.util.List;

public class LambdaDeployer {

    public static void uploadLambdaPackagesToS3Bucket(Path tempProjectDirName, List<BundleDescriptor> descriptors) {

        S3Client s3Client = S3Client.builder().credentialsProvider(ProfileCredentialsProvider.create()).build();
        String bucketName = tempProjectDirName.getFileName().toString();

        CreateBucketRequest createBucketRequest = CreateBucketRequest
                .builder()
                .bucket(bucketName)
                .createBucketConfiguration(CreateBucketConfiguration.builder()
                        .build())
                .build();
        s3Client.createBucket(createBucketRequest);

        descriptors.forEach(descriptor -> {
            Path lambdaPacakgePath = tempProjectDirName.resolve("deployment-bundles")
                    .resolve("parachutes")
                    .resolve(descriptor.getParachuteName().toLowerCase())
                    .resolve("target")
                    .resolve(descriptor.getBuildScript().getArtifactName());

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(descriptor.getBuildScript().getArtifactName())
                            .build(),
                    RequestBody.fromFile(lambdaPacakgePath));
        });
    }
}