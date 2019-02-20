package org.parachutesmethod.framework.extraction.filehandling;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectCodeExplorer {

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectCodeExplorer.class);

    private String glob;
    private List<Path> sourceCodeFiles;

    public ProjectCodeExplorer(SupportedLanguage lang) {
        this.glob = lang.getGlobbingPattern();
        this.sourceCodeFiles = new ArrayList<>();
    }

    public void collectProjectFiles(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, glob)) {
            for (Path entry : stream) {
                sourceCodeFiles.add(entry);
            }
        } catch (DirectoryIteratorException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public void handleSourceFiles(SourceCodeHandler pathHandler) {
        for (Path entry : sourceCodeFiles) {
            pathHandler.handle(entry);
        }
    }

}

