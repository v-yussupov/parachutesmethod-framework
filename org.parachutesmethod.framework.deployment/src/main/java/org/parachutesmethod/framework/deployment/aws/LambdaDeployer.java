package org.parachutesmethod.framework.deployment.aws;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.parachutesmethod.framework.common.Util;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
            Path lambdaPackagePath = tempProjectDirName.resolve("deployment-bundles")
                    .resolve("parachutes")
                    .resolve(descriptor.getParachuteName().toLowerCase())
                    .resolve("target")
                    .resolve(descriptor.getBuildScript().getArtifactName());

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(descriptor.getBuildScript().getArtifactName())
                            .build(),
                    RequestBody.fromFile(lambdaPackagePath));
        });
    }

    public static void deploySAMTemplate(Path tempProjectDirName) {
        try {
            Path depModelsPath = tempProjectDirName.resolve("deployment-bundles")
                    .resolve("deployment-models");

            // TODO: make it not ugly
            String packageCmd = "sam package --template-file template.yml --output-template-file packaged.yml --s3-bucket " + tempProjectDirName.getFileName().toString();
            String deployCmd = "sam deploy --template-file packaged.yml --stack-name " + tempProjectDirName.getFileName().toString().concat("-stack") + " --capabilities CAPABILITY_IAM";

            Util.invokeShellCommand(depModelsPath, packageCmd);
            Util.invokeShellCommand(depModelsPath, deployCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}