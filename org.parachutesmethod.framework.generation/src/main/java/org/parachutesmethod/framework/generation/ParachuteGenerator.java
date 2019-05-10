package org.parachutesmethod.framework.generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import org.parachutesmethod.framework.common.FileExtension;
import org.parachutesmethod.framework.deployment.aws.LambdaDeployer;
import org.parachutesmethod.framework.extraction.ExtractionSetting;
import org.parachutesmethod.framework.generation.generators.aws.CloudFormationGenerator;
import org.parachutesmethod.framework.generation.generators.aws.LambdaPackageGenerator;
import org.parachutesmethod.framework.generation.generators.aws.SAMTemplateGenerator;
import org.parachutesmethod.framework.generation.generators.routers.NginxRouterGenerator;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParachuteGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteGenerator.class);

    private Path tempProjectDirPath;
    private Path descriptorsPath;
    private SupportedCloudProvider provider;
    private List<BundleDescriptor> parachuteDescriptors = new ArrayList<>();

    public ParachuteGenerator(String tempProjectDirPath, String provider) {
        this.tempProjectDirPath = Paths.get(tempProjectDirPath);
        this.descriptorsPath = this.tempProjectDirPath.resolve(ExtractionSetting.DESCRIPTORS_FOLDER_NAME.value());
        this.provider = SupportedCloudProvider.getValue(provider);
    }

    public void generate(boolean deploy) throws IOException {
        deserializeBundleDescriptors();
        generateParachuteBundles(deploy);
    }

    private void deserializeBundleDescriptors() throws IOException {
        List<Path> parachuteProjectDirectories = Files.list(descriptorsPath)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        parachuteProjectDirectories.forEach(dir -> {
            Path descriptorPath = dir.resolve(ExtractionSetting.DESCRIPTOR_NAME.value().concat(FileExtension.JSON.extension()));

            try {
                LOGGER.info("Starting to deserialize the parachute descriptor");
                ObjectMapper mapper = new ObjectMapper();
                BundleDescriptor descriptor = mapper.readValue(descriptorPath.toFile(), BundleDescriptor.class);
                parachuteDescriptors.add(descriptor);
                LOGGER.info("Finished deserializing the parachute descriptor for parachute: " + descriptor.getParachuteName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateParachuteBundles(boolean deploy) throws IOException {
        if (provider.equals(SupportedCloudProvider.AWS)) {
            Path bundlesDirectory = tempProjectDirPath.resolve(Constants.DEPLOYMENT_BUNDLES_FOLDER);
            LambdaPackageGenerator generator = new LambdaPackageGenerator(bundlesDirectory, parachuteDescriptors);
            generator.generate();

            Map<String, String> routerConfigurations = generateRouterConfiguration(bundlesDirectory, parachuteDescriptors);

            Path cloudFormationTemplatesDir = bundlesDirectory.resolve(Constants.DEPLOYMENT_MODELS_FOLDER);
            Files.createDirectory(cloudFormationTemplatesDir);
            try {
                CloudFormationGenerator.generateCloudFormationTemplate(cloudFormationTemplatesDir.toString(), routerConfigurations);
                SAMTemplateGenerator.generate(tempProjectDirPath.getFileName().toString(), parachuteDescriptors, cloudFormationTemplatesDir);

                if (deploy) {

                    LambdaDeployer.uploadLambdaPackagesToS3Bucket(tempProjectDirPath, parachuteDescriptors);
                    LambdaDeployer.deploySAMTemplate(tempProjectDirPath);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, String> generateRouterConfiguration(Path bundlesDir, List<BundleDescriptor> descriptors) {
        Map<String, String> routerConfigurations = new HashMap<>();
        Path parachuteDir = bundlesDir.resolve(Constants.ROUTER_CONFIGURATIONS_FOLDER);
        try {
            Files.createDirectory(parachuteDir);
            routerConfigurations = NginxRouterGenerator.generateNginxRouterConfigurationFiles(parachuteDir.toString(), descriptors);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return routerConfigurations;
    }

    public Path getDescriptorsPath() {
        return descriptorsPath;
    }
}