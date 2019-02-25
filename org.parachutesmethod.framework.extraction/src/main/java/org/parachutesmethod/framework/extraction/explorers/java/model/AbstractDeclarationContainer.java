package org.parachutesmethod.framework.extraction.explorers.java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class AbstractDeclarationContainer<T> {
    JavaProjectFile containingFile;
    protected String name;
    TypeDeclaration parent;
    T declaration;
    List<JavaAnnotation> annotations = new ArrayList<>();

    AbstractDeclarationContainer() {
    }

    public JavaProjectFile getContainingFile() {
        return containingFile;
    }

    public String getName() {
        return name;
    }

    public TypeDeclaration getParent() {
        return parent;
    }

    public boolean hasParent() {
        return Objects.nonNull(parent);
    }

    public T getDeclaration() {
        return declaration;
    }

    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 37). // two randomly chosen prime numbers
                append(name).
                append(containingFile.getPackageName()).
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
                append(containingFile.getPackageName(), javaClass.getContainingFile().getPackageName()).
                isEquals();
    }
}
