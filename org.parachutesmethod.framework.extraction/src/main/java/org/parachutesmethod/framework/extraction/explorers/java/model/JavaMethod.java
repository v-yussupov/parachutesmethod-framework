package org.parachutesmethod.framework.extraction.explorers.java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.parachutesmethod.framework.extraction.Constants;

public class JavaMethod {

    private JavaProjectFile parentFile;
    private String parentDeclarationName;
    private boolean classAsParentDeclaration;
    private String name;
    private List<JavaAnnotation> annotations;
    private boolean isParachuteMethod;
    private MethodDeclaration methodDeclaration;
    private Type returnType;
    private List<Parameter> inputParameters;

    JavaMethod(JavaProjectFile parent, MethodDeclaration methodDeclaration) {
        this.parentFile = parent;
        this.methodDeclaration = methodDeclaration;
        this.name = methodDeclaration.getNameAsString();
        this.annotations = new ArrayList<>();
        findAnnotations();
        this.returnType = methodDeclaration.getType();
        this.inputParameters = methodDeclaration.getParameters();

        if (methodDeclaration.getParentNode().isPresent()) {
            ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
            parentDeclarationName = cd.getNameAsString();
            classAsParentDeclaration = !cd.isInterface();
        }
    }

    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    public Optional<JavaAnnotation> getParachuteAnnotation() {
        return annotations.stream()
                .filter(JavaAnnotation::isParachuteAnnotation)
                .findFirst();
    }

    public boolean isParachuteMethod() {
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

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public JavaProjectFile getParentFile() {
        return parentFile;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Parameter> getInputParameters() {
        return inputParameters;
    }

    public String getParentDeclarationName() {
        return parentDeclarationName;
    }

    public boolean isClassAsParentDeclaration() {
        return classAsParentDeclaration;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 31). // two randomly chosen prime numbers
                append(name).
                append(parentFile.getFileName()).
                append(parentFile.getFilePath().toString()).
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
                append(parentFile.getFileName(), method.getParentFile().getFileName()).
                append(parentFile.getFilePath().toString(), method.getParentFile().getFilePath().toString()).
                isEquals();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Method: %s, Annotations_Count: %d, hasParachuteAnnotation: %s\n", name, annotations.size(), isParachuteMethod));
        sb.append(String.format("Parent ClassOrInterface Name: %s\n", parentDeclarationName));
        sb.append(String.format("Parent Declaration is a Class: %s\n", classAsParentDeclaration));
        sb.append(System.lineSeparator());
        sb.append(String.format("Method body: \n%s\n", methodDeclaration));
        return sb.toString();
    }
}
