package org.parachutesmethod.framework.extraction;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaAnnotation;
import org.parachutesmethod.framework.extraction.explorers.java.model.JavaMethod;

@JsonIgnoreProperties( {"preparedParachute", "parachuteMethodData"})
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

        MethodDeclaration md = parachuteMethodData.getMethodDeclaration();
        if (Objects.nonNull(parachuteAnnotations) && parachuteAnnotations.isRetainParachuteAnnotations()) {
            parachuteMethodData.getAnnotations()
                    .stream()
                    .filter(JavaAnnotation::isParachuteAnnotation)
                    .forEach(a -> md.addAnnotation(a.getAnnotationExpression()));
        } else if (Objects.nonNull(parachuteAnnotations) && parachuteAnnotations.isRetainAnnotations()) {
            parachuteMethodData.getAnnotations()
                    .stream()
                    .filter(a -> !a.isParachuteAnnotation())
                    .forEach(a -> md.addAnnotation(a.getAnnotationExpression()));
        } else {
            md.setAnnotations(new NodeList<>());
        }
        classDeclaration.getMembers().add(md);

        //parachuteMethodData.getInputParameters()
    }

    @JsonProperty
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
