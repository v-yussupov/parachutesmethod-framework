package org.parachutesmethod.framework.extraction.explorers.java.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaParachuteProject {

    private List<JavaProjectFile> projectFiles = new ArrayList<>();

    public JavaParachuteProject(List<Path> projectFilePaths) throws IOException {
        for (Path path : projectFilePaths) {
            projectFiles.add(new JavaProjectFile(path));
        }
    }

    public List<JavaProjectFile> getFiles() {
        return projectFiles;
    }

    public void printProjectFiles() {
        projectFiles.forEach(System.out::println);
    }
}