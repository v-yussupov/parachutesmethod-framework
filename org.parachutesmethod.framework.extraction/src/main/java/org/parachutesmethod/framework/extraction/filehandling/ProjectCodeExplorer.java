package org.parachutesmethod.framework.extraction.filehandling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectCodeExplorer {

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectCodeExplorer.class);

    private String fileExtension;
    private List<Path> projectFilePaths;

    public ProjectCodeExplorer(SupportedLanguage language) {
        this.fileExtension = language.getFileExtension();
        this.projectFilePaths = new ArrayList<>();
    }

    public void traverseProjectFiles(Path dir, SourceCodeProcessor handler) throws IOException {
        Files.find(dir,
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().endsWith(fileExtension))
                .forEach(
                        entry -> {
                            LOGGER.info(String.format("Found matching entry with extension %s and path: %s", fileExtension, entry.toString()));
                            handler.handle(entry);
                            projectFilePaths.add(entry);
                        }
                );
    }
}

