package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.parachutesmethod.framework.extraction.Constants;

import java.util.ArrayList;
import java.util.List;

public class JavaMethod {

    private JavaProjectFile parent;
    private String name;
    private List<JavaAnnotation> annotations;
    private boolean isParachuteMethod;
    private MethodDeclaration methodDeclaration;
    private Type returnType;
    private List<JavaClass> requestResponsePOJOs = new ArrayList<>();
    private List<Parameter> inputParameters;

    JavaMethod(JavaProjectFile parent, MethodDeclaration methodDeclaration) {
        this.parent = parent;
        this.methodDeclaration = methodDeclaration;
        this.name = methodDeclaration.getNameAsString();
        this.annotations = new ArrayList<>();
        findAnnotations();
        this.returnType = methodDeclaration.getType();
        this.inputParameters = methodDeclaration.getParameters();
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

    public String getName() {
        return name;
    }

    public JavaProjectFile getParent() {
        return parent;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Parameter> getInputParameters() {
        return inputParameters;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 31). // two randomly chosen prime numbers
                append(name).
                append(parent.getFileName()).
                append(parent.getFilePath().toString()).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JavaMethod))
            return false;
        if (obj == this)
            return true;

        JavaMethod method = (JavaMethod) obj;
        return new EqualsBuilder().
                append(name, method.getName()).
                append(parent.getFileName(), method.getParent().getFileName()).
                append(parent.getFilePath().toString(), method.getParent().getFilePath().toString()).
                isEquals();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Method: %s, Annotations_Count: %d, hasParachuteAnnotation: %s", name, annotations.size(), isParachuteMethod));
        sb.append(System.lineSeparator());
        sb.append(String.format("Method body: \n%s\n", methodDeclaration));
        return sb.toString();
    }
}
