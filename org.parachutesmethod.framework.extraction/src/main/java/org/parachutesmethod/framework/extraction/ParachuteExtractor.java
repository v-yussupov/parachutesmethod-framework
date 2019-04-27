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
import org.parachutesmethod.framework.common.BuildScript;
import org.parachutesmethod.framework.common.FileExtension;
import org.parachutesmethod.framework.extraction.exceptions.LangSupportException;
import org.parachutesmethod.framework.extraction.exceptions.ProjectParsingException;
import org.parachutesmethod.framework.extraction.exceptions.WrongRepositoryException;
import org.parachutesmethod.framework.extraction.languages.SupportedLanguage;
import org.parachutesmethod.framework.extraction.languages.java.JavaProjectExplorer;
import org.parachutesmethod.framework.models.java.JavaConfiguration;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import org.parachutesmethod.framework.models.java.projectmodel.JavaClass;
import org.parachutesmethod.framework.models.java.projectmodel.JavaMethod;
import org.parachutesmethod.framework.models.java.projectmodel.MavenProjectObjectModel;
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
import java.util.Optional;
import java.util.Set;

public class ParachuteExtractor<T> {

    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteExtractor.class);

    private T repositoryLocation;
    private SupportedLanguage lang;
    private Path tempRootDirectoryPath;
    private Path tempClonedProjectPath;

    public ParachuteExtractor(T repositoryLocation, String lang) throws LangSupportException {
        this.repositoryLocation = repositoryLocation;
        this.lang = SupportedLanguage.getValue(lang);
    }

    public Path extract() throws GitAPIException, WrongRepositoryException, IOException, ProjectParsingException {
        cloneRepository();
        return extractParachutes();
    }

    /**
     * Clones a given repository into a temporary folder
     *
     * @throws IOException              an exception occurred while working with file system
     * @throws GitAPIException          an exception occurred while downloading a GitHub repository
     * @throws WrongRepositoryException an exception occurred due to the unsupported repository type
     */
    private void cloneRepository() throws IOException, GitAPIException, WrongRepositoryException {
        if (repositoryLocation instanceof URL) {
            downloadGitHubRepository((URL) repositoryLocation);
        } else if (repositoryLocation instanceof Path) {
            // TODO copy local repository to a temp folder for processing
            LOGGER.info("local repositories are not yet supported");
        } else {
            throw new WrongRepositoryException("Exception occurred while cloning repository: provided location is not supported");
        }
    }

    /**
     * Downloads a given GitHub repository
     *
     * @param url link to the GitHub repository
     * @throws IOException     an exception occurred while working with file system
     * @throws GitAPIException an exception occurred while downloading a GitHub repository
     */
    private void downloadGitHubRepository(URL url) throws IOException, GitAPIException {

        String[] tokens = url.toString().split("/");
        String repositoryName = tokens[tokens.length - 1];

        tempRootDirectoryPath = Files.createTempDirectory(repositoryName + "-");
        Path tempProjectDir = tempRootDirectoryPath.resolve(ExtractionSetting.SOURCE_PROJECT_FOLDER.value());

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

    /**
     * Parses the downloaded repository and extracts annotated parachute functionalities from the code base
     *
     * @return path to the folder containing parsed provider-agnostic parachute bundles
     * @throws IOException             an exception occurred while working with file system
     * @throws ProjectParsingException an exception occurred while parsing the project
     */
    private Path extractParachutes() throws IOException, ProjectParsingException {
        LOGGER.info("Starting to parse the project directory");

        if (Objects.nonNull(lang) && SupportedLanguage.JAVA.equals(lang)) {
            JavaProjectExplorer explorer = new JavaProjectExplorer(this.tempClonedProjectPath);

            // TODO log project details using LOGGER
            explorer.printProjectDetails();

            Optional<Path> parachuteBundlesPath = generateJavaParachuteBundles(explorer);
            if (parachuteBundlesPath.isPresent()) {
                return parachuteBundlesPath.get();
            } else {
                throw new ProjectParsingException("Something went wrong during parsing the project");
            }
        }

        throw new ProjectParsingException("Extractor for a given language could not be found");
    }

    private Optional<Path> generateJavaParachuteBundles(JavaProjectExplorer explorer) throws ProjectParsingException {
        if (explorer.hasParachutes()) {
            Path tempPath = tempRootDirectoryPath.resolve(ExtractionSetting.GENERATION_BUNDLES_FOLDER.value());
            explorer.getParachuteMethods().forEach(parachuteMethod -> {
                processParachute(tempPath, explorer, parachuteMethod);
            });

            return Optional.of(tempPath);
        }
        throw new ProjectParsingException("Project contains no parachute annotations");
    }

    private void processParachute(Path tempParachuteGenerationBundlesPath, JavaProjectExplorer explorer, JavaMethod parachuteMethod) {
        try {
            // prepare directories
            BundleDescriptor descriptor = new BundleDescriptor(parachuteMethod);

            String fileName = createParachuteFileName(descriptor.getName());
            Path dir = tempParachuteGenerationBundlesPath.resolve(descriptor.getName().toLowerCase());
            Files.createDirectories(dir);

            Files.createFile(dir.resolve(fileName));

            List<JavaClass> dependencyClasses = mapRequestResponseClasses(explorer.getProjectClasses(), parachuteMethod);
            List<JavaClass> staticClasses = new ArrayList<>();

            dependencyClasses.forEach(depClass -> {
                String pojoName = depClass.getName().concat(SupportedLanguage.JAVA.getFileExtension());
                try {
                    if (!depClass.getClassDeclaration().isStatic()) {
                        //Files.createFile(dir.resolve(pojoName));

                        CompilationUnit cu = new CompilationUnit();
                        cu.setPackageDeclaration(JavaConfiguration.EXTRACTED_PARACHUTE_PACKAGE_NAME.value());

                        NodeList<ImportDeclaration> imports = new NodeList<>();
                        depClass.getContainingFile().getImports().forEach(i -> imports.add(i.getImportDeclaration()));
                        cu.setImports(imports);
                        cu.addType(depClass.getClassDeclaration());

                        writeContentToFile(dir.resolve(pojoName).toFile(), cu.toString());
                    } else {
                        staticClasses.add(depClass);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Extract parachute code
            if (staticClasses.isEmpty()) {
                writeContentToFile(dir.resolve(fileName).toFile(), descriptor.getPreparedParachute().toString());
            } else {
                staticClasses.forEach(sc -> {
                    CompilationUnit cu = descriptor.getPreparedParachute();
                    cu.getType(0).getMembers().add(sc.getClassDeclaration());
                    try {
                        writeContentToFile(dir.resolve(fileName).toFile(), cu.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            // Extract parachute meta-data
            Path spec = dir.resolve(ExtractionSetting.BUNDLE_SPECFILE_NAME.value().concat(FileExtension.JSON.extension()));
            Files.createFile(spec);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(spec.toFile(), descriptor);

            // Extract maven dependencies
            Path pom = dir.resolve(BuildScript.MAVEN.value().concat(FileExtension.XML.extension()));
            Files.createFile(pom);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pom.toFile()), StandardCharsets.UTF_8))) {
                Model model = new Model();
                model.setName("parachute-method." + descriptor.getName().toLowerCase());
                model.setModelVersion("4.0.0");
                model.setGroupId(JavaConfiguration.EXTRACTED_PARACHUTE_PACKAGE_NAME.value());
                model.setArtifactId(JavaConfiguration.EXTRACTED_PARACHUTE_PACKAGE_NAME.value().concat("-").concat(descriptor.getName().toLowerCase()));
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
    }

    private List<JavaClass> mapRequestResponseClasses(Set<JavaClass> projectClasses, JavaMethod parachuteMethod) {
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

        projectClasses.forEach(c -> {
            if (parachuteMethod.getMethodDeclaration().toString().contains(c.getName())) {
                result.add(c);
            }
        });

        return result;
    }

    private String createParachuteFileName(String parachuteName) {
        return parachuteName.concat(SupportedLanguage.JAVA.getFileExtension());
    }

    private void createParachuteBundleFolder(String parachuteName) {

    }

    private void writeContentToFile(File file, String content) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }

    private void getGitHubRepositoryLanguage() throws IOException, LangSupportException {
        GitHub github = GitHub.connect();
        GHRepository repo = github.getRepository(repositoryLocation.toString());
        this.lang = SupportedLanguage.getValue(repo.getLanguage());
    }

    public Path getTempRootDirectoryPath() {
        return tempRootDirectoryPath;
    }

    public Path getTempClonedProjectPath() {
        return tempClonedProjectPath;
    }
}