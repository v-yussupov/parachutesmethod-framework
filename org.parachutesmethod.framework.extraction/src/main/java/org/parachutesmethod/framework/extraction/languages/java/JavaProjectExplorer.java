package org.parachutesmethod.framework.extraction.languages.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.parachutesmethod.framework.common.BuildScript;
import org.parachutesmethod.framework.common.FileExtension;
import org.parachutesmethod.framework.extraction.languages.ProjectCodeExplorer;
import org.parachutesmethod.framework.extraction.languages.SupportedLanguage;
import org.parachutesmethod.framework.extraction.languages.java.visitors.ClassOrInterfaceDeclarationCollector;
import org.parachutesmethod.framework.extraction.languages.java.visitors.ImportDeclarationCollector;
import org.parachutesmethod.framework.models.java.projectmodel.JavaClass;
import org.parachutesmethod.framework.models.java.projectmodel.JavaImport;
import org.parachutesmethod.framework.models.java.projectmodel.JavaInterface;
import org.parachutesmethod.framework.models.java.projectmodel.JavaMethod;
import org.parachutesmethod.framework.models.java.projectmodel.JavaProjectFile;
import org.parachutesmethod.framework.models.java.projectmodel.MavenProjectObjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaProjectExplorer extends ProjectCodeExplorer {
    private static Logger LOGGER = LoggerFactory.getLogger(JavaProjectExplorer.class);

    private boolean hasParachutes;
    private List<JavaProjectFile> projectFiles = new ArrayList<>();
    private Set<JavaClass> projectClasses = new HashSet<>();
    private Set<JavaInterface> projectInterfaces = new HashSet<>();
    private Set<String> packageNames = new HashSet<>();
    private List<MavenProjectObjectModel> pomFiles = new ArrayList<>();

    private TypeSolver combinedTypeSolver;

    public JavaProjectExplorer(Path projectPath) throws IOException {
        super(projectPath, SupportedLanguage.JAVA);

        LOGGER.info("project src folder:" + projectPath.resolve("src"));

        combinedTypeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(projectPath.resolve("src/main/java").toFile())
        );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setAttributeComments(false)
                .setDoNotAssignCommentsPrecedingEmptyLines(true)
                .setSymbolResolver(symbolSolver);
        JavaParser.setStaticConfiguration(parserConfiguration);

        parseProjectFiles();

        // Assumption: Maven is used as a build tool
        // TODO add support for, e.g., Gradle & other options
        parseMavenPOMFiles();
    }

    private void parseProjectFiles() throws IOException {
        for (Path path : this.findProjectFiles()) {
            try (FileInputStream in = new FileInputStream(path.toString())) {
                LOGGER.info(String.format("Starting to parse project file %s", path.getFileName().toString()));
                CompilationUnit parsedFile = JavaParser.parse(in);

                JavaProjectFile projectFile = new JavaProjectFile(path, parsedFile);
                packageNames.add(projectFile.getPackageName());
                projectFile.setImports(findJavaImports(parsedFile));
                projectFile.processJavaClassesAndInterfaces(findJavaClassesAndInterfaces(parsedFile));

                this.hasParachutes |= projectFile.isWithParachutes();
                this.projectClasses.addAll(projectFile.getClasses());
                this.projectInterfaces.addAll(projectFile.getInterfaces());
                this.projectFiles.add(projectFile);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void parseMavenPOMFiles() throws IOException {
        findProjectFiles(FileExtension.XML.extension())
                .stream()
                .filter((filePath) -> filePath.getFileName().toString().equals(BuildScript.MAVEN.value()))
                .collect(Collectors.toList())
                .forEach(pomPath -> {
                    try (FileInputStream in = new FileInputStream(pomPath.toString())) {
                        LOGGER.info(String.format("Starting to parse Maven pom file located at %s", pomPath.getFileName().toString()));
                        MavenXpp3Reader reader = new MavenXpp3Reader();
                        pomFiles.add(new MavenProjectObjectModel(pomPath, reader.read(in)));
                    } catch (IOException | XmlPullParserException e) {
                        LOGGER.error(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private List<JavaImport> findJavaImports(CompilationUnit parsedFile) {
        List<ImportDeclaration> importDeclarations = new ArrayList<>();
        List<JavaImport> imports = new ArrayList<>();
        VoidVisitor<List<ImportDeclaration>> importDeclarationCollector = new ImportDeclarationCollector();
        importDeclarationCollector.visit(parsedFile, importDeclarations);
        if (!importDeclarations.isEmpty()) {
            importDeclarations.forEach(
                    importDeclaration -> imports.add(new JavaImport(importDeclaration))
            );
        }
        return imports;
    }

    private List<ClassOrInterfaceDeclaration> findJavaClassesAndInterfaces(CompilationUnit parsedFile) {
        List<ClassOrInterfaceDeclaration> classDeclarations = new ArrayList<>();
        VoidVisitor<List<ClassOrInterfaceDeclaration>> classDeclarationCollector = new ClassOrInterfaceDeclarationCollector();
        classDeclarationCollector.visit(parsedFile, classDeclarations);
        return classDeclarations;
    }

    public List<JavaProjectFile> getProjectFiles() {
        return projectFiles;
    }

    public Set<String> getPackageNames() {
        return packageNames;
    }

    public List<MavenProjectObjectModel> getPomFiles() {
        return pomFiles;
    }

    public Set<JavaClass> getProjectClasses() {
        return projectClasses;
    }

    public JavaClass getProjectClassByName(String name) {
        for (JavaClass c : projectClasses) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public Set<JavaMethod> getParachuteMethods() {
        Set<JavaMethod> parachuteMethods = new HashSet<>();
        projectFiles.forEach(f -> parachuteMethods.addAll(
                f.getFileMethods()
                        .stream()
                        .filter(JavaMethod::isParachuteMethod)
                        .collect(Collectors.toList()))
        );
        return parachuteMethods;
    }

    public boolean hasParachutes() {
        return hasParachutes;
    }

    public ResolvedType resolveType(Type type) {
        return JavaParserFacade.get(combinedTypeSolver).convertToUsage(type);
    }

    private void printProjectFiles() {
        projectFiles.forEach(System.out::println);
    }

    private void printProjectClasses() {
        projectClasses.forEach(System.out::println);
    }

    private void printProjectInterfaces() {
        projectInterfaces.forEach(System.out::println);
    }

    private void printParachuteMethods() {
        getParachuteMethods().forEach(System.out::println);
    }

    public void printProjectDetails() {
        LOGGER.info("Project Files");
        printProjectFiles();
        LOGGER.info("Project Classes");
        printProjectClasses();
        LOGGER.info("Project Interfaces");
        printProjectInterfaces();
        LOGGER.info("Parachute methods");
        printParachuteMethods();
    }
}