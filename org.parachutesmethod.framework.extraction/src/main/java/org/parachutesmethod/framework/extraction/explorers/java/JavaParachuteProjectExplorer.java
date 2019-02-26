package org.parachutesmethod.framework.extraction.explorers.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.parachutesmethod.framework.extraction.explorers.ProjectCodeExplorer;
import org.parachutesmethod.framework.extraction.explorers.SupportedLanguage;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaClass;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaInterface;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaMethod;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaProjectFile;
import org.parachutesmethod.framework.extraction.explorers.java.model.MavenPOMFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaParachuteProjectExplorer extends ProjectCodeExplorer {
    private static Logger LOGGER = LoggerFactory.getLogger(JavaParachuteProjectExplorer.class);

    private boolean hasParachutes;
    private List<JavaProjectFile> projectFiles = new ArrayList<>();
    private Set<JavaClass> projectClasses = new HashSet<>();
    private Set<JavaInterface> projectInterfaces = new HashSet<>();
    private List<MavenPOMFile> pomFiles = new ArrayList<>();

    public JavaParachuteProjectExplorer(Path projectPath) throws IOException {
        super(projectPath, SupportedLanguage.JAVA);

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setAttributeComments(false)
                .setDoNotAssignCommentsPrecedingEmptyLines(true)
                .setSymbolResolver(symbolSolver);
        JavaParser.setStaticConfiguration(parserConfiguration);

        parseProjectFiles();
    }

    private void parseProjectFiles() throws IOException {
        for (Path path : this.findProjectFiles()) {
            try (FileInputStream in = new FileInputStream(path.toString())) {
                LOGGER.info(String.format("Starting to parse project file %s", path.getFileName().toString()));
                CompilationUnit parsedFile = JavaParser.parse(in);

                JavaProjectFile projectFile = new JavaProjectFile(path, parsedFile);

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
        for (Path path : findMavenPOMFiles()) {
            try (FileInputStream in = new FileInputStream(path.toString())) {
                LOGGER.info(String.format("Starting to parse project file %s", path.getFileName().toString()));
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public List<JavaProjectFile> getProjectFiles() {
        return projectFiles;
    }

    public Set<JavaClass> getProjectClasses() {
        return projectClasses;
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

    public void printProjectFiles() {
        projectFiles.forEach(System.out::println);
    }

    public void printProjectClasses() {
        projectClasses.forEach(System.out::println);
    }

    public void printProjectInterfaces() {
        projectInterfaces.forEach(System.out::println);
    }

    public void printParachuteMethods() {
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