package org.parachutesmethod.framework.extraction.explorers.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.parachutesmethod.framework.extraction.explorers.ProjectCodeExplorer;
import org.parachutesmethod.framework.extraction.explorers.SupportedLanguage;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaClass;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaMethod;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaProjectFile;
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

public class JavaParachuteProjectExplorer extends ProjectCodeExplorer {
    private static Logger LOGGER = LoggerFactory.getLogger(JavaParachuteProjectExplorer.class);

    private boolean hasParachutes;
    private List<JavaProjectFile> projectFiles = new ArrayList<>();
    private Set<JavaClass> projectClasses = new HashSet<>();

    public JavaParachuteProjectExplorer(Path projectPath) {
        super(projectPath, SupportedLanguage.JAVA);

        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setAttributeComments(false)
                .setDoNotAssignCommentsPrecedingEmptyLines(true);
        JavaParser.setStaticConfiguration(parserConfiguration);
    }

    public void parseProject() throws IOException {
        for (Path path : this.findProjectFiles()) {
            try (FileInputStream in = new FileInputStream(path.toString())) {
                LOGGER.info(String.format("Starting to parse project file %s", path.getFileName().toString()));
                CompilationUnit parsedFile = JavaParser.parse(in);

                JavaProjectFile projectFile = new JavaProjectFile(path, parsedFile);

                this.hasParachutes |= projectFile.isWithParachutes();
                this.projectClasses.addAll(projectFile.getClasses());
                this.projectFiles.add(projectFile);

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
                f.getMethods()
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

    public void printProjectMethods() {
        getParachuteMethods().forEach(System.out::println);
    }
}