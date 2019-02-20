package org.parachutesmethod.framework.extraction;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.parachutesmethod.framework.extraction.analyzers.java.JavaParachuteProjectAnalyzer;
import org.parachutesmethod.framework.extraction.filehandling.SupportedLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParachuteExtractor<T> {

    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteExtractor.class);

    private T repositoryLocation;
    private SupportedLanguage lang;

    public ParachuteExtractor(T repositoryLocation) {
        this.repositoryLocation = repositoryLocation;
    }

    public Path getRepository() throws IOException, GitAPIException {
        if (repositoryLocation instanceof URL) {
            getGitHubRepositoryLanguage();
            return downloadGitHubRepository((URL) repositoryLocation);
        } else {
            // TODO copy local repository to a temp folder for processing
            return null;
        }
    }

    private void getGitHubRepositoryLanguage() throws IOException {
        GitHub github = GitHub.connect();
        GHRepository repo = github.getRepository(repositoryLocation.toString());
        this.lang = SupportedLanguage.getValue(repo.getLanguage());
    }

    private Path downloadGitHubRepository(URL url) throws IOException, GitAPIException {

        String[] tokens = url.toString().split("/");
        String repositoryName = tokens[tokens.length - 1];

        Path tempDir = Files.createTempDirectory(repositoryName + "-");

        LOGGER.info("Cloning " + url + " into " + tempDir.toString());
        try (Git git = Git.cloneRepository()
                .setURI(url.toString())
                .setDirectory(tempDir.toFile())
                .call()) {
            LOGGER.info("Repository cloned successfully to " + tempDir.toAbsolutePath().toString());

            return tempDir;
        } catch (GitAPIException e) {
            LOGGER.info("Exception occurred while cloning repo", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void parseParachuteProject(Path projectPath) {
        if (Objects.nonNull(lang) && SupportedLanguage.JAVA.equals(lang)) {
            JavaParachuteProjectAnalyzer analyzer = new JavaParachuteProjectAnalyzer(projectPath);


        }
    }
}
