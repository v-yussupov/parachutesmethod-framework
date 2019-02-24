package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class JavaClass {
    private String name;
    private String packageName;
    private TypeDeclaration parent;
    private ClassOrInterfaceDeclaration body;
    private List<JavaClass> innerClasses = new ArrayList<>();

    public JavaClass(ClassOrInterfaceDeclaration cd, String packageName) {
        this.name = cd.getNameAsString();
        this.packageName = packageName;
        this.body = cd;
        if (cd.isNestedType() && cd.getParentNode().isPresent()) {
            this.parent = (TypeDeclaration) cd.getParentNode().get();
        }
    }

    public List<JavaClass> getDirectInnerClasses() {
        return innerClasses;
    }

    public void setDirectInnerClasses(List<JavaClass> innerClasses) {
        this.innerClasses = innerClasses;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isStaticClass() {
        return body.isStatic();
    }

    public boolean isNested() {
        return body.isNestedType();
    }

    public TypeDeclaration getParent() {
        return parent;
    }

    public ClassOrInterfaceDeclaration getBody() {
        return body;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 37). // two randomly chosen prime numbers
                append(name).
                append(packageName).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JavaClass))
            return false;
        if (obj == this)
            return true;

        JavaClass javaClass = (JavaClass) obj;
        return new EqualsBuilder().
                append(name, javaClass.getName()).
                append(packageName, javaClass.getPackageName()).
                isEquals();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Class: %s, Package: %s", name, packageName));
        sb.append(System.lineSeparator());
        sb.append(String.format("Inner classes count: \n%d\n", innerClasses.size()));
        sb.append(System.lineSeparator());
        innerClasses.forEach(c -> {
            sb.append(String.format("Inner class: \n%s\n", c.getName()));
            sb.append(System.lineSeparator());
        });

        return sb.toString();
    }

}