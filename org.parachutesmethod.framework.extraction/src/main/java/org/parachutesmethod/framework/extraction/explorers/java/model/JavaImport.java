package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.ast.ImportDeclaration;

public class JavaImport {

    private String name;
    private ImportDeclaration importDeclaration;

    JavaImport(ImportDeclaration importDeclaration) {
        this.name = importDeclaration.getNameAsString();
        this.importDeclaration = importDeclaration;

    }

    public ImportDeclaration getImportDeclaration() {
        return importDeclaration;
    }

    public String getName() {
        return name;
    }
}
