package org.parachutesmethod.framework.extraction.explorers.java.model;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class JavaInterface extends AbstractDeclarationContainer<ClassOrInterfaceDeclaration> {

    protected List<JavaMethod> methods = new ArrayList<>();

    public JavaInterface(JavaProjectFile containingFile, ClassOrInterfaceDeclaration cd) {
        super();
        this.name = cd.getNameAsString();
        this.containingFile = containingFile;
        this.declaration = cd;
        if (!cd.getMethods().isEmpty()) {
            cd.getMethods().forEach(md -> methods.add(new JavaMethod(containingFile, md)));
        }
    }

    List<JavaMethod> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Interface: %s, Package: %s", name, containingFile.getPackageName()));
        sb.append(System.lineSeparator());
        sb.append(String.format("Methods_Count: %d\n", methods.size()));

        return sb.toString();
    }
}
