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
    private SupportedLanguage lang;
    private Path tempRootPath;
    private Path tempProjectPath;

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

        tempRootPath = Files.createTempDirectory(repositoryName + "-");
        Path tempProjectDir = tempRootPath.resolve(Constants.SOURCE_PROJECT_FOLDER);

        LOGGER.info("Cloning " + url + " into " + tempProjectDir.toString());
        try (Git ignored = Git.cloneRepository()
                .setURI(url.toString())
                .setDirectory(tempProjectDir.toFile())
                .call()) {
            LOGGER.info("Repository cloned successfully to " + tempProjectDir.toAbsolutePath().toString());
            this.tempProjectPath = tempProjectDir;
        } catch (GitAPIException e) {
            LOGGER.info("Exception occurred while cloning GitHub repository", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void parseParachuteProject() throws IOException {
        LOGGER.info("Starting to parse the project directory");
        if (Objects.nonNull(lang) && SupportedLanguage.JAVA.equals(lang)) {
            JavaParachuteProjectExplorer explorer = new JavaParachuteProjectExplorer(this.tempProjectPath);
            explorer.parseProject();

            LOGGER.info("Project Files");
            explorer.printProjectFiles();

            LOGGER.info("Project Classes");
            explorer.printProjectClasses();

            LOGGER.info("Parachute methods");
            explorer.printProjectMethods();

            if (explorer.hasParachutes()) {
                //TODO continue extraction process
                // forEach parachute:
                // 1. analyze inputs and outputs
                //    -- if POJOs are used as inputs -> POJOs have to be included as separate .java files

            } else {
                //TODO complete extraction process
            }
        }
    }

    public Path getTempRootPath() {
        return tempRootPath;
    }
}