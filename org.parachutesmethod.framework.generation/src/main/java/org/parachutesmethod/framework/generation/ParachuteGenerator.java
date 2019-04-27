package org.parachutesmethod.framework.generation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.parachutesmethod.framework.extraction.languages.SupportedLanguage;
import org.parachutesmethod.framework.extraction.languages.java.JavaProjectExplorer;
import org.parachutesmethod.framework.generation.generators.faas.aws.AWSGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParachuteGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteGenerator.class);

    private Path path;
    private String projectLanguage;
    private SupportedCloudProvider provider;
    private List<JavaProjectExplorer> parachuteProjectExplorers = new ArrayList<>();

    public ParachuteGenerator(String path, String projectLanguage, String provider) {
        this.path = Paths.get(path);
        this.projectLanguage = projectLanguage;
        this.provider = SupportedCloudProvider.getValue(provider);
    }

    public void parseParachuteProject() throws IOException {
        List<Path> parachuteProjectFolders = Files.list(path)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        SupportedLanguage language = provider.checkSupport(projectLanguage);

        if (Objects.nonNull(language)) {
            if (language.equals(SupportedLanguage.JAVA)) {
                LOGGER.info("Starting to parse Java parachute projects");
                parachuteProjectFolders.forEach(parachuteProjectFolder -> {
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
                });
            }
        }
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

    public String getProjectLanguage() {
        return projectLanguage;
    }
}