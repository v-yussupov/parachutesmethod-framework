package org.parachutesmethod.framework.extraction.analyzers.java;

import java.io.IOException;
import java.nio.file.Path;

import org.parachutesmethod.framework.extraction.filehandling.ProjectCodeExplorer;
import org.parachutesmethod.framework.extraction.filehandling.SupportedLanguage;

public class JavaParachuteProjectAnalyzer {

    private Path projectPath;

    public JavaParachuteProjectAnalyzer(Path projectPath) {
        this.projectPath = projectPath;
    }

    public void traverseProject() throws IOException {

        ProjectCodeExplorer explorer = new ProjectCodeExplorer(SupportedLanguage.JAVA);
        explorer.traverseProjectFiles(projectPath, (p) -> System.out.println(p.toString()));

        //JavaParser.parsePackageDeclaration(null);
    }
}
