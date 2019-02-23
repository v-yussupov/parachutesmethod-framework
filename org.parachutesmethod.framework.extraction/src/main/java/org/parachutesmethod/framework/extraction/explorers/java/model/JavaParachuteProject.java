package org.parachutesmethod.framework.extraction.explorers.java.model;

import java.util.List;

public class JavaParachuteProject {

    private List<JavaProjectFile> projectFiles;
    private boolean withParachutes;

    public JavaParachuteProject(List<JavaProjectFile> projectFiles) {
        this.projectFiles = projectFiles;
        this.withParachutes = projectFiles.stream().anyMatch(JavaProjectFile::isWithParachutes);
    }

    public List<JavaProjectFile> getFiles() {
        return projectFiles;
    }

    public void printProjectFiles() {
        projectFiles.forEach(System.out::println);
    }

    public boolean isWithParachutes() {
        return withParachutes;
    }

    public void prepareParachuteMethods() {

    }

}