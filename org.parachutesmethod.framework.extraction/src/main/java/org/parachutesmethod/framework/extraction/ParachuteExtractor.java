package org.parachutesmethod.framework.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.parachutesmethod.framework.extraction.exceptions.NotSupportedLanguageException;
import org.parachutesmethod.framework.extraction.exceptions.NotSupportedRepositoryTypeException;
import org.parachutesmethod.framework.extraction.explorers.SupportedLanguage;
import org.parachutesmethod.framework.extraction.explorers.java.JavaParachuteProjectExplorer;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaClass;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaMethod;
import org.parachutesmethod.framework.extraction.explorers.java.model.MavenProjectObjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

                Path tempParachuteGenerationBundlesPath = tempRootDirectoryPath.resolve(Constants.GENERATION_BUNDLES_FOLDER);

                explorer.getParachuteMethods().forEach(parachuteMethod -> {
                    try {
                        List<JavaClass> pojos = findClassesForExtraction(explorer.getProjectClasses(), parachuteMethod);

                        // Extract parachute code
                        ParachuteMethodDescriptor descriptor = new ParachuteMethodDescriptor(parachuteMethod);
                        String fileName = descriptor.getParachuteName().concat(SupportedLanguage.JAVA.getFileExtension());
                        Path dir = tempParachuteGenerationBundlesPath.resolve(descriptor.getParachuteName());
                        Files.createDirectories(dir);
                        Files.createFile(dir.resolve(fileName));
                        writeContentToFile(dir.resolve(fileName).toFile(), descriptor.getPreparedParachute().toString());

                        pojos.forEach(pojo -> {
                            String pojoName = pojo.getName().concat(SupportedLanguage.JAVA.getFileExtension());
                            try {
                                Files.createFile(dir.resolve(pojoName));

                                CompilationUnit cu = new CompilationUnit();
                                cu.setPackageDeclaration(Constants.EXTRACTED_PARACHUTE_PACKAGE_NAME);

                                NodeList<ImportDeclaration> imports = new NodeList<>();
                                pojo.getContainingFile().getImports().forEach(i -> imports.add(i.getImportDeclaration()));
                                cu.setImports(imports);
                                cu.addType(pojo.getClassDeclaration());

                                writeContentToFile(dir.resolve(pojoName).toFile(), cu.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        // Extract parachute meta-data
                        Path spec = dir.resolve(Constants.PARACHUTE_METADATA_FILE.concat(Constants.EXTENSION_JSON));
                        Files.createFile(spec);
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writerWithDefaultPrettyPrinter().writeValue(spec.toFile(), descriptor);

                        // Extract maven dependencies
                        Path pom = dir.resolve(Constants.MAVEN_POM.concat(Constants.EXTENSION_XML));
                        Files.createFile(pom);
                        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pom.toFile()), StandardCharsets.UTF_8))) {
                            Model model = new Model();
                            model.setName("parachute-method." + descriptor.getParachuteName().toLowerCase());
                            model.setModelVersion("4.0.0");
                            model.setGroupId(Constants.EXTRACTED_PARACHUTE_PACKAGE_NAME);
                            model.setArtifactId(Constants.EXTRACTED_PARACHUTE_PACKAGE_NAME.concat("-").concat(descriptor.getParachuteName().toLowerCase()));
                            model.setVersion("1.0-SNAPSHOT");

                            Set<Dependency> dependencies = new HashSet<>();
                            for (MavenProjectObjectModel m : explorer.getPomFiles()) {
                                dependencies.addAll(m.getDependencies());

                                for (Object prop : m.getProperties().keySet()) {
                                    model.addProperty((String) prop, m.getProperties().getProperty((String) prop));
                                }
                            }
                            dependencies.forEach(model::addDependency);

                            new MavenXpp3Writer().write(writer, model);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }

    public void extractParachutes() throws GitAPIException, NotSupportedRepositoryTypeException, IOException {
        cloneRepository();
        parseProject();
    }

    public List<JavaClass> findClassesForExtraction(Set<JavaClass> projectClasses, JavaMethod parachuteMethod) {
        List<String> parsedTypes = new ArrayList<>();

        parachuteMethod.getInputParameters().forEach(p -> parsedTypes.add(p.getType().asString()));
        parsedTypes.add(parachuteMethod.getReturnType().asString());

        List<JavaClass> result = new ArrayList<>();
        for (String name : parsedTypes) {
            projectClasses.forEach(c -> {
                if (c.getName().equals(name)) {
                    result.add(c);
                }
            });
        }

        return result;
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