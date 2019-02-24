package org.parachutesmethod.framework.extraction;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaMethod;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaProjectFile;

public class ParachuteMethodDescriptor {

    private String parachuteName;
    private CompilationUnit preparedParachute;

    public ParachuteMethodDescriptor(JavaMethod parachute) {
        this.parachuteName = parachute.getName();
        JavaProjectFile file = parachute.getParentFile();

        CompilationUnit cu = new CompilationUnit();
        NodeList<ImportDeclaration> imports = new NodeList<>();
        parachute.getParentFile().getImports().forEach(i -> imports.add(i.getImportDeclaration()));
        cu.setImports(imports);

        ClassOrInterfaceDeclaration classDeclaration = cu.addClass(parachute.getParentDeclarationName());

        MethodDeclaration md = parachute.getMethodDeclaration();

        md.setAnnotations(new NodeList<>());
        classDeclaration.getMembers().add(md);
        preparedParachute = cu;
    }

    public String getParachuteName() {
        return parachuteName;
    }

    public CompilationUnit getPreparedParachute() {
        return preparedParachute;
    }
}
