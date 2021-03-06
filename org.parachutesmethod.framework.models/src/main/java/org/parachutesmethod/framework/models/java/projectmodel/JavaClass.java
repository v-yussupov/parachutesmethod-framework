package org.parachutesmethod.framework.models.java.projectmodel;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.parachutesmethod.framework.models.java.JavaConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JavaClass extends AbstractDeclarationContainer<ClassOrInterfaceDeclaration> {
    private String packageName;
    private List<JavaMethod> methods = new ArrayList<>();
    private List<JavaClass> innerClasses = new ArrayList<>();
    private boolean withParachutes;
    private String resourcePath;

    public JavaClass(JavaProjectFile containingFile, ClassOrInterfaceDeclaration cd) {
        this.packageName = containingFile.getPackageName();
        this.name = cd.getNameAsString();

        this.containingFile = containingFile;
        this.declaration = cd;

        if (cd.isNestedType() && cd.getParentNode().isPresent()) {
            this.parent = (TypeDeclaration) cd.getParentNode().get();
        }

        if (!cd.getAnnotations().isEmpty()) {
            cd.getAnnotations().forEach(a -> {
                if (a.getNameAsString().equals(JavaConfiguration.PATH_ANNOTATION.value())) {
                    resourcePath = a.asSingleMemberAnnotationExpr().getMemberValue().toString().replace("\"", "");
                }
                JavaAnnotation annotation = new JavaAnnotation(a);
                annotations.add(annotation);
            });
        }

        if (!cd.getMethods().isEmpty()) {
            cd.getMethods().forEach(md -> {
                JavaMethod method = new JavaMethod(containingFile, this, md);
                methods.add(method);
                withParachutes |= method.isParachuteMethod();
            });
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFullClassName() {
        TypeDeclaration parent = this.getParent();

        StringBuilder innerClassesString = new StringBuilder();
        while (Objects.nonNull(parent)) {
            innerClassesString.append(parent.getNameAsString());
            innerClassesString.append(".");
            Optional<Node> newParent = parent.getParentNode();
            if (newParent.isPresent() && newParent.get() instanceof TypeDeclaration) {
                parent = (TypeDeclaration) newParent.get();
            } else {
                parent = null;
            }
        }

        if (innerClassesString.length() > 0) {
            return packageName.concat(".").concat(innerClassesString.toString()).concat(name);
        } else {
            return packageName.concat(".").concat(name);
        }
    }

    List<JavaMethod> getMethods() {
        return methods;
    }

    public TypeDeclaration getParent() {
        return parent;
    }

    public boolean isStaticClass() {
        return declaration.isStatic();
    }

    public boolean isNested() {
        return declaration.isNestedType();
    }

    boolean isWithParachutes() {
        return withParachutes;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public List<JavaClass> getInnerClasses() {
        return innerClasses;
    }

    void setInnerClasses(List<JavaClass> innerClasses) {
        this.innerClasses = innerClasses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Class: %s, Package: %s\n", name, packageName));
        sb.append(String.format("Methods_Count: %d\n", methods.size()));
        sb.append(String.format("HasParachutes: %s\n", withParachutes));
        if (Objects.nonNull(resourcePath)) {
            sb.append(String.format("ResourcePath: %s", resourcePath));
        }
        sb.append(System.lineSeparator());
        sb.append(String.format("Inner classes count: %d\n", innerClasses.size()));
        innerClasses.forEach(c -> {
            sb.append(String.format("Inner class: %s", c.getName()));
            sb.append(System.lineSeparator());
        });

        return sb.toString();
    }

}