package org.parachutesmethod.framework.generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.parachutesmethod.framework.common.FileExtension;
import org.parachutesmethod.framework.extraction.ExtractionSetting;
import org.parachutesmethod.framework.extraction.languages.SupportedLanguage;
import org.parachutesmethod.framework.extraction.languages.java.JavaProjectExplorer;
import org.parachutesmethod.framework.generation.generators.faas.aws.AWSGenerator;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParachuteGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteGenerator.class);

    private Path path;
    private SupportedCloudProvider provider;
    private List<JavaProjectExplorer> parachuteProjectExplorers = new ArrayList<>();

    public ParachuteGenerator(String path, String provider) {
        this.path = Paths.get(path);
        this.provider = SupportedCloudProvider.getValue(provider);
    }

    public void parseParachuteProject() throws IOException {
        List<Path> parachuteProjectDirectories = Files.list(path)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        List<BundleDescriptor> parachuteDescriptors = new ArrayList<>();
        parachuteProjectDirectories.forEach(dir -> {
            Path descriptorPath = dir.resolve(ExtractionSetting.BUNDLE_SPECFILE_NAME.value().concat(FileExtension.JSON.extension()));

            try {
                ObjectMapper mapper = new ObjectMapper();
                BundleDescriptor descriptor = mapper.readValue(descriptorPath.toFile(), BundleDescriptor.class);
                parachuteDescriptors.add(descriptor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        parachuteDescriptors.forEach(descriptor -> {
            SupportedLanguage language = provider.checkSupport(descriptor.getProgrammingLanguage());
            if (Objects.nonNull(language)) {
                if (language.equals(SupportedLanguage.JAVA)) {
                    LOGGER.info("Starting to parse Java parachute projects");
                    LOGGER.info(descriptor.toString());

                    /*parachuteProjectDirectories.forEach(parachuteProjectFolder -> {
                        try {
                            LOGGER.info("Parsing parachute project name: " + parachuteProjectFolder.getFileName().toString());
                            JavaProjectExplorer explorer = new JavaProjectExplorer(parachuteProjectFolder);
                            parachuteProjectExplorers.add(explorer);

                            String mainClass = parachuteProjectFolder.getFileName().toString();
                            explorer.getProjectClassByName(mainClass);
                            LOGGER.info("parsedPrimaryFile:" + explorer.getProjectClassByName(mainClass).getClassDeclaration());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });*/
                }
            }
        });
    }

    public void generateParachuteBundles() throws IOException {
        AWSGenerator.generate(path, parachuteProjectExplorers);
    }

    public Path getPath() {
        return path;
    }

    public SupportedCloudProvider getProvider() {
        return provider;
    }

    public List<JavaProjectExplorer> getParachuteProjectExplorers() {
        return parachuteProjectExplorers;
    }
}