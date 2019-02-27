package org.parachutesmethod.framework.models.java.projectmodel;

import com.github.javaparser.ast.ImportDeclaration;

public class JavaImport {

    private String name;
    private ImportDeclaration importDeclaration;

    public JavaImport(ImportDeclaration importDeclaration) {
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
