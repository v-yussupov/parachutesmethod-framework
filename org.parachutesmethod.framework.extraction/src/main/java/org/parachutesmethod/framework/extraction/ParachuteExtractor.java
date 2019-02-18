package org.parachutesmethod.framework.extraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParachuteExtractor implements RemoteLinkProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteExtractor.class);

    public ParachuteExtractor() {

    }

    @Override
    public boolean downloadGitHubRepository(String url) throws IOException {

        String[] tokens = url.split("/");
        String repositoryName = tokens[tokens.length - 1];

        Path tempDir = Files.createTempDirectory(repositoryName + "-");

        LOGGER.info("Cloning " + url + " into " + tempDir.toString());
        try (Git git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(tempDir.toFile())
                .call()) {
            LOGGER.info("Repository cloned successfully to " + tempDir.toAbsolutePath().toString());
            return true;
        } catch (GitAPIException e) {
            LOGGER.info("Exception occurred while cloning repo", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
