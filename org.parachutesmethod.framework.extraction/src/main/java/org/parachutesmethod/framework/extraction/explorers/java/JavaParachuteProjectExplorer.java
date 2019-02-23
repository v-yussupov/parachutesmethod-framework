package org.parachutesmethod.framework.extraction.explorers.java;

import java.io.IOException;
import java.nio.file.Path;

import org.parachutesmethod.framework.extraction.explorers.java.model.JavaParachuteProject;
import org.parachutesmethod.framework.extraction.explorers.ProjectCodeExplorer;
import org.parachutesmethod.framework.extraction.explorers.SupportedLanguage;

public class JavaParachuteProjectExplorer extends ProjectCodeExplorer {

    private JavaParachuteProject project;

    public JavaParachuteProjectExplorer(Path projectPath) throws IOException {
        super(projectPath, SupportedLanguage.JAVA);
        this.project = new JavaParachuteProject(this.findProjectFiles());
        project.printProjectFiles();
    }

    public JavaParachuteProject getProject() {
        return project;
    }
}