package org.parachutesmethod.framework.extraction.languages;

import org.parachutesmethod.framework.common.FileExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class ProjectCodeExplorer {

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectCodeExplorer.class);

    private Path projectPath;
    private SupportedLanguage projectLanguage;

    public ProjectCodeExplorer(Path projectPath, SupportedLanguage projectLanguage) {
        this.projectPath = projectPath;
        this.projectLanguage = projectLanguage;
    }

    protected List<Path> findProjectFiles() throws IOException {
        return findProjectFiles(FileExtension.valueOf(projectLanguage.name()).extension());
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

    public Path getProjectPath() {
        return projectPath;
    }

    public SupportedLanguage getProjectLanguage() {
        return projectLanguage;
    }
}