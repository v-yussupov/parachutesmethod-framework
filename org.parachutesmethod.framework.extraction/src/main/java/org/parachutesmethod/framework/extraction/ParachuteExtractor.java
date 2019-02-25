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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ParachuteExtractor<T> {

    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteExtractor.class);

    private T repositoryLocation;
    private SupportedLanguage lang;
    private Path tempRootDirectoryPath;
    private Path tempClonedProjectPath;

    public ParachuteExtractor(T repositoryLocation, String lang) throws NotSupportedLanguageException {
        this.repositoryLocation = repositoryLocation;
        this.lang = SupportedLanguage.getValue(lang);
    }

    private void cloneRepository() throws IOException, GitAPIException, NotSupportedRepositoryTypeException {
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

        tempRootDirectoryPath = Files.createTempDirectory(repositoryName + "-");
        Path tempProjectDir = tempRootDirectoryPath.resolve(Constants.SOURCE_PROJECT_FOLDER);

        LOGGER.info("Cloning " + url + " into " + tempProjectDir.toString());
        try (Git ignored = Git.cloneRepository()
                .setURI(url.toString())
                .setDirectory(tempProjectDir.toFile())
                .call()) {
            LOGGER.info("Repository cloned successfully to " + tempProjectDir.toAbsolutePath().toString());
            this.tempClonedProjectPath = tempProjectDir;
        } catch (GitAPIException e) {
            LOGGER.info("Exception occurred while cloning GitHub repository", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void parseProject() throws IOException {
        LOGGER.info("Starting to parse the project directory");
        if (Objects.nonNull(lang) && SupportedLanguage.JAVA.equals(lang)) {
            JavaParachuteProjectExplorer explorer = new JavaParachuteProjectExplorer(this.tempClonedProjectPath);
            explorer.printProjectDetails();

            if (explorer.hasParachutes()) {
                //TODO continue extraction process
                // forEach parachute:
                // 1. analyze inputs and outputs
                //    -- if POJOs are used as inputs -> POJOs have to be included as separate .java files

                Path tempParachuteGenerationBundlesPath = tempRootDirectoryPath.resolve(Constants.GENERATION_BUNDLES_FOLDER);

                explorer.getParachuteMethods().forEach(parachuteMethod -> {
                    try {
                        ParachuteMethodDescriptor descriptor = new ParachuteMethodDescriptor(parachuteMethod);
                        String fileName = descriptor.getParachuteName().concat(SupportedLanguage.JAVA.getFileExtension());
                        Path dir = tempParachuteGenerationBundlesPath.resolve(descriptor.getParachuteName());
                        Files.createDirectories(dir);
                        Files.createFile(dir.resolve(fileName));
                        writeContentToFile(dir.resolve(fileName).toFile(), descriptor.getPreparedParachute().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } else {
                //TODO stop extraction process
            }
        }
    }

    public void extractParachutes() throws GitAPIException, NotSupportedRepositoryTypeException, IOException {
        cloneRepository();
        parseProject();

    }

    public Path getTempRootDirectoryPath() {
        return tempRootDirectoryPath;
    }

    public Path getTempClonedProjectPath() {
        return tempClonedProjectPath;
    }

    private void writeContentToFile(File file, String content) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }

}