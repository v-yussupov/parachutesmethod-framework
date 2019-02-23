package org.parachutesmethod.framework.extraction;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.parachutesmethod.framework.extraction.exceptions.NotSupportedLanguageException;
import org.parachutesmethod.framework.extraction.exceptions.NotSupportedRepositoryTypeException;
import org.parachutesmethod.framework.extraction.explorers.SupportedLanguage;
import org.parachutesmethod.framework.extraction.explorers.java.JavaParachuteProjectExplorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ParachuteExtractor<T> {

    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteExtractor.class);

    private T repositoryLocation;
    private Path tempRepositoryPath;
    private SupportedLanguage lang;

    public ParachuteExtractor(T repositoryLocation, String lang) throws NotSupportedLanguageException {
        this.repositoryLocation = repositoryLocation;
        this.lang = SupportedLanguage.getValue(lang);
    }

    public void cloneRepository() throws IOException, GitAPIException, NotSupportedRepositoryTypeException {
        if (repositoryLocation instanceof URL) {
            downloadGitHubRepository((URL) repositoryLocation);
        } else if (repositoryLocation instanceof Path) {
            // TODO copy local repository to a temp folder for processing
            LOGGER.info("local repositories are not yet supported");
        } else {
            throw new NotSupportedRepositoryTypeException("Exception occurred while cloning repository: provided location is not supported");
        }
    }

    private void getGitHubRepositoryLanguage() throws IOException, NotSupportedLanguageException {
        GitHub github = GitHub.connect();
        GHRepository repo = github.getRepository(repositoryLocation.toString());
        this.lang = SupportedLanguage.getValue(repo.getLanguage());
    }

    private void downloadGitHubRepository(URL url) throws IOException, GitAPIException {

        String[] tokens = url.toString().split("/");
        String repositoryName = tokens[tokens.length - 1];

        Path tempDir = Files.createTempDirectory(repositoryName + "-");

        LOGGER.info("Cloning " + url + " into " + tempDir.toString());
        try (Git ignored = Git.cloneRepository()
                .setURI(url.toString())
                .setDirectory(tempDir.toFile())
                .call()) {
            LOGGER.info("Repository cloned successfully to " + tempDir.toAbsolutePath().toString());
            this.tempRepositoryPath = tempDir;
        } catch (GitAPIException e) {
            LOGGER.info("Exception occurred while cloning GitHub repository", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void parseParachuteProject() throws IOException {
        LOGGER.info("Starting to parse the project directory");
        if (Objects.nonNull(lang) && SupportedLanguage.JAVA.equals(lang)) {
            JavaParachuteProjectExplorer explorer = new JavaParachuteProjectExplorer(this.tempRepositoryPath);
            explorer.parseProject();
            explorer.getProject().printProjectFiles();

            if (explorer.getProject().isWithParachutes()) {
                //TODO continue extraction process


            } else {
                //TODO complete extraction process
            }
        }
    }


}
