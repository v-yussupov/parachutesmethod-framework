package org.parachutesmethod.framework.extraction.analyzers.java;

import java.io.IOException;
import java.nio.file.Path;

import org.parachutesmethod.framework.extraction.filehandling.SupportedLanguage;
import org.parachutesmethod.framework.extraction.filehandling.SourceCodeHandler;
import org.parachutesmethod.framework.extraction.filehandling.ProjectCodeExplorer;

public class JavaParachuteProjectAnalyzer {

    private Path projectPath;

    public JavaParachuteProjectAnalyzer(Path projectPath) {
        this.projectPath = projectPath;
    }

    public void traverseProject(SourceCodeHandler handler) throws IOException {

        ProjectCodeExplorer explorer = new ProjectCodeExplorer(SupportedLanguage.JAVA);
        explorer.collectProjectFiles(projectPath);

        explorer.handleSourceFiles(handler);

        //JavaParser.parsePackageDeclaration(null);
    }
}
