package org.parachutesmethod.framework.extraction.explorers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProjectCodeExplorer {

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectCodeExplorer.class);

    private Path projectPath;
    private SupportedLanguage projectLanguage;

    public ProjectCodeExplorer(Path projectPath, SupportedLanguage projectLanguage) {
        this.projectPath = projectPath;
        this.projectLanguage = projectLanguage;
    }

    protected List<Path> findProjectFiles() throws IOException {
        return findProjectFiles(projectLanguage.getFileExtension());
    }

    protected List<Path> findProjectFiles(String fileExtension) throws IOException {
        List<Path> projectFilePaths = new ArrayList<>();
        Files.find(
                projectPath,
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().endsWith(fileExtension)
        ).forEach(
                entry -> {
                    LOGGER.info(String.format("Found matching entry with extension %s and path: %s", fileExtension, entry.toString()));
                    projectFilePaths.add(entry);
                }
        );
        return projectFilePaths;
    }

    protected List<Path> findMavenPOMFiles() throws IOException {
        return findProjectFiles(".xml")
                .stream()
                .filter((filePath) -> "pom".equals(filePath.getFileName().toString()))
                .collect(Collectors.toList());
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public SupportedLanguage getProjectLanguage() {
        return projectLanguage;
    }
}