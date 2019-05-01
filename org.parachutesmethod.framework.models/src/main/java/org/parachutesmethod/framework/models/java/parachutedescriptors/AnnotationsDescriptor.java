package org.parachutesmethod.framework.models.java.parachutedescriptors;

import org.parachutesmethod.framework.models.java.projectmodel.JavaAnnotation;

import java.util.ArrayList;
import java.util.List;

public class AnnotationsDescriptor {
    private List<JavaAnnotation> annotations = new ArrayList<>();

    public AnnotationsDescriptor() {
    }

    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<JavaAnnotation> annotations) {
        this.annotations = annotations;
    }

    public void addAnnotation(JavaAnnotation annotation) {
        annotations.add(annotation);
    }
}
