package org.parachutesmethod.framework.extraction;

import java.util.Objects;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaMethod;

public class ParachuteMethodDescriptor {

    private String parachuteName;
    private JavaMethod parachuteMethodData;
    private CompilationUnit preparedParachute;
    private ParachuteMethodAnnotationDescriptor parachuteAnnotations;

    ParachuteMethodDescriptor(JavaMethod parachute) {
        parachuteName = parachute.getName();
        preparedParachute = new CompilationUnit();
        parachuteMethodData = parachute;
        parachute.getParachuteAnnotation()
                .ifPresent(javaAnnotation ->
                        parachuteAnnotations = new ParachuteMethodAnnotationDescriptor(
                                javaAnnotation.getParameters()
                        )
                );

        setImports();
        constructClassWithParachute();
    }

    private void setImports() {
        NodeList<ImportDeclaration> imports = new NodeList<>();
        parachuteMethodData.getParentFile().getImports().forEach(i -> imports.add(i.getImportDeclaration()));
        preparedParachute.setImports(imports);
    }

    private void constructClassWithParachute() {
        ClassOrInterfaceDeclaration classDeclaration = preparedParachute.addClass(parachuteMethodData.getParentDeclarationName());

        //new ClassOrInterfaceDeclaration(Modifier.createModifierList(Modifier.Keyword.PUBLIC), false, parachuteMethodData.getParentDeclarationName());
        MethodDeclaration md = parachuteMethodData.getMethodDeclaration();
        if (Objects.nonNull(parachuteAnnotations) && !parachuteAnnotations.isRetainParachuteAnnotations()) {
            md.setAnnotations(new NodeList<>());
        } /*else {
            for (JavaAnnotation a : parachuteMethodData.getAnnotations()) {
                md.addAnnotation(a.getAnnotationExpression());
            }
        }*/
        classDeclaration.getMembers().add(md);

        //parachuteMethodData.getInputParameters()
    }

    String getParachuteName() {
        return parachuteName;
    }

    CompilationUnit getPreparedParachute() {
        return preparedParachute;
    }

    public ParachuteMethodAnnotationDescriptor getParachuteAnnotations() {
        return parachuteAnnotations;
    }
}
