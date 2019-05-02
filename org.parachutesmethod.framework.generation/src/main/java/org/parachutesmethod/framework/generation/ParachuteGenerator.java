package org.parachutesmethod.framework.generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.parachutesmethod.framework.common.FileExtension;
import org.parachutesmethod.framework.extraction.ExtractionSetting;
import org.parachutesmethod.framework.generation.generators.aws.AWSLambdaGenerator;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParachuteGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteGenerator.class);

    private Path path;
    private SupportedCloudProvider provider;
    private List<BundleDescriptor> parachuteDescriptors = new ArrayList<>();

    public ParachuteGenerator(String path, String provider) {
        this.path = Paths.get(path);
        this.provider = SupportedCloudProvider.getValue(provider);
    }

    public void generate() throws IOException {
        deserializeBundleDescriptors();
        generateParachuteBundles();
    }

    private void deserializeBundleDescriptors() throws IOException {
        List<Path> parachuteProjectDirectories = Files.list(path)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        parachuteProjectDirectories.forEach(dir -> {
            Path descriptorPath = dir.resolve(ExtractionSetting.BUNDLE_SPECFILE_NAME.value().concat(FileExtension.JSON.extension()));

            try {
                LOGGER.info("Starting to deserialize the parachute descriptor");
                ObjectMapper mapper = new ObjectMapper();
                BundleDescriptor descriptor = mapper.readValue(descriptorPath.toFile(), BundleDescriptor.class);
                parachuteDescriptors.add(descriptor);
                LOGGER.info("Finished deserializing the parachute descriptor for parachute: %s", descriptor.getParachuteName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateParachuteBundles() throws IOException {
        if (provider.equals(SupportedCloudProvider.AWS)) {
            AWSLambdaGenerator.generate(path, parachuteDescriptors);
        }
    }
}