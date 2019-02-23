package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.parachutesmethod.framework.extraction.Constants;

import java.util.ArrayList;
import java.util.List;

public class JavaMethod {

    private JavaProjectFile parent;
    private String name;
    private List<JavaAnnotation> annotations;
    private boolean isParachuteMethod;
    private MethodDeclaration methodDeclaration;

    JavaMethod(JavaProjectFile parent, MethodDeclaration methodDeclaration) {
        this.parent = parent;
        this.methodDeclaration = methodDeclaration;
        this.name = methodDeclaration.getNameAsString();
        this.annotations = new ArrayList<>();
        findAnnotations();
    }

    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    boolean isParachuteMethod() {
        return isParachuteMethod;
    }

    private void findAnnotations() {
        if (methodDeclaration.isAnnotationPresent(Constants.PARACHUTE_METHOD_ANNOTATION)) {
            this.isParachuteMethod = true;
        }
        if (methodDeclaration.getAnnotations().isNonEmpty()) {
            methodDeclaration.getAnnotations().forEach(a -> {
                JavaAnnotation annotation = new JavaAnnotation(a);
                isParachuteMethod = annotation.isParachuteAnnotation();
                annotations.add(new JavaAnnotation(a));
            });
        }
    }

    @Override
    public String toString() {
        return String.format("Method: %s, Annotations_Count: %d, hasParachuteAnnotation: %s", name, annotations.size(), isParachuteMethod);
    }
}
