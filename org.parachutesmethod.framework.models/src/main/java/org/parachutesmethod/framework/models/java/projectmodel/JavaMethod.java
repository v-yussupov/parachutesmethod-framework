package org.parachutesmethod.framework.models.java.projectmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.parachutesmethod.framework.models.java.JavaConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JavaMethod {

    private JavaProjectFile parentFile;
    private JavaClass parentClass;
    @JsonProperty
    private String name;
    @JsonProperty
    private String parentDeclarationName;
    private boolean classAsParentDeclaration;
    private List<JavaAnnotation> annotations;
    private boolean isParachuteMethod;
    private MethodDeclaration methodDeclaration;
    private Type returnType;
    private List<Parameter> inputParameters;
    private String resourcePath;

    public JavaMethod() {
    }

    JavaMethod(JavaProjectFile parent, JavaClass parentClass, MethodDeclaration methodDeclaration) {
        this.parentFile = parent;
        this.parentClass = parentClass;
        this.methodDeclaration = methodDeclaration;
        this.name = methodDeclaration.getNameAsString();
        this.annotations = new ArrayList<>();
        findAnnotations();

        if (!annotations.isEmpty()) {
            annotations.forEach(a -> {
                if (a.isPathAnnotation()) {
                    String path = a.getAnnotationExpression().asSingleMemberAnnotationExpr().getMemberValue().toString().replace("\"", "");
                    resourcePath = findResourcePath(parentClass, path);
                }
            });
        }

        this.returnType = methodDeclaration.getType();
        this.inputParameters = methodDeclaration.getParameters();

        if (methodDeclaration.getParentNode().isPresent()) {
            ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
            parentDeclarationName = cd.getNameAsString();
            classAsParentDeclaration = !cd.isInterface();
        }
    }

    private String findResourcePath(JavaClass parentClass, String currentPath) {
        if (Objects.isNull(parentClass)) {
            return currentPath;
        }
        
        Optional<JavaAnnotation> parentPathAnnotation = parentClass.getAnnotations().stream().filter(JavaAnnotation::isPathAnnotation).findFirst();
        if (parentPathAnnotation.isPresent()) {
            String parentResPath = parentPathAnnotation.get().getAnnotationExpression().asSingleMemberAnnotationExpr().getMemberValue().toString().replace("\"", "");
            if (!parentResPath.isEmpty()) {
                String[] parentTokens = parentResPath.split("/");
                String[] currentTokens = currentPath.split("/");
                String normalized = "/";
                for (String s : parentTokens) {
                    if (!s.isEmpty()) {
                        normalized = normalized.concat(s).concat("/");
                    }
                }
                for (String s : currentTokens) {
                    if (!s.isEmpty()) {
                        normalized = normalized.concat(s).concat("/");
                    }
                }
                currentPath = normalized;
            }
        }

        if (parentClass.getParent() instanceof ClassOrInterfaceDeclaration) {
            return findResourcePath(new JavaClass(parentFile, (ClassOrInterfaceDeclaration) parentClass.getParent()), currentPath);
        } else {
            return findResourcePath(null, currentPath);
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
        if (methodDeclaration.isAnnotationPresent(JavaConfiguration.PARACHUTE_METHOD_ANNOTATION.value())) {
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

    public String getResourcePath() {
        return resourcePath;
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
        sb.append(String.format("Resource Path: %s\n", resourcePath));
        sb.append(System.lineSeparator());
        sb.append(String.format("Method body: \n%s\n", methodDeclaration));
        return sb.toString();
    }

    public JavaClass getParentClass() {
        return parentClass;
    }
}
