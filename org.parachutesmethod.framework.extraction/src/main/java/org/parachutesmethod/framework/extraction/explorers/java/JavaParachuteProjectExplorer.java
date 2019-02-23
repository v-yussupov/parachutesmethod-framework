package org.parachutesmethod.framework.extraction.explorers.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.parachutesmethod.framework.extraction.explorers.ProjectCodeExplorer;
import org.parachutesmethod.framework.extraction.explorers.SupportedLanguage;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaParachuteProject;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaProjectFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaParachuteProjectExplorer extends ProjectCodeExplorer {
    private static Logger LOGGER = LoggerFactory.getLogger(JavaParachuteProjectExplorer.class);

    private JavaParachuteProject project;

    public JavaParachuteProjectExplorer(Path projectPath) {
        super(projectPath, SupportedLanguage.JAVA);
    }

    public void parseProject() throws IOException {
        List<JavaProjectFile> projectFiles = new ArrayList<>();

        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setAttributeComments(false)
                .setDoNotAssignCommentsPrecedingEmptyLines(true);
        JavaParser.setStaticConfiguration(parserConfiguration);

        for (Path path : this.findProjectFiles()) {

            try (FileInputStream in = new FileInputStream(path.toString())) {
                LOGGER.info(String.format("Starting to parse project file %s", path.getFileName().toString()));
                CompilationUnit parsedFile = JavaParser.parse(in);
                projectFiles.add(new JavaProjectFile(path, parsedFile));
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }
        this.project = new JavaParachuteProject(projectFiles);
    }

    public JavaParachuteProject getProject() {
        return project;
    }
}