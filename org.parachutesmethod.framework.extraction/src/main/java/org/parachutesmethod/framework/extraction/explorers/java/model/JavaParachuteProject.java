package org.parachutesmethod.framework.extraction.explorers.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaParachuteProject {

    private List<JavaProjectFile> projectFiles;
    private boolean withParachutes;
    private Set<JavaClass> projectClasses = new HashSet<>();
    private Set<JavaMethod> parachuteMethods = new HashSet<>();

    public JavaParachuteProject(List<JavaProjectFile> projectFiles) {
        this.projectFiles = projectFiles;
        this.withParachutes = projectFiles.stream().anyMatch(JavaProjectFile::isWithParachutes);
        projectFiles.forEach(f -> {
            projectClasses.addAll(f.getClasses());
            parachuteMethods.addAll(
                    f.getMethods().stream().filter(JavaMethod::isParachuteMethod).collect(Collectors.toList())
            );
        });
    }

    public List<JavaProjectFile> getFiles() {
        return projectFiles;
    }

    public Set<JavaClass> getProjectClasses() {
        return projectClasses;
    }

    public void printProjectFiles() {
        projectFiles.forEach(System.out::println);
    }

    public boolean isWithParachutes() {
        return withParachutes;
    }

    public Set<JavaMethod> getParachuteMethods() {
        return parachuteMethods;
    }

    public void prepareParachuteMethods() {

    }

}