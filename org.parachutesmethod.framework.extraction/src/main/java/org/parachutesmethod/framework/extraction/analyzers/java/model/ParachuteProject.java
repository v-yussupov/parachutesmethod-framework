package org.parachutesmethod.framework.extraction.analyzers.java.model;

import java.util.List;

public class ParachuteProject {

    private List<ProjectClass> classes;
    //private List<NameExpr> annotatedMethods;

    public List<ProjectClass> getClasses() {
        return classes;
    }

    public void setClasses(List<ProjectClass> classes) {
        this.classes = classes;
    }
}
