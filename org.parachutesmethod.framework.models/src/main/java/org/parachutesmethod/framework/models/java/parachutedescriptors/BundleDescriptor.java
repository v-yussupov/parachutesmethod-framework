package org.parachutesmethod.framework.models.java.parachutedescriptors;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.parachutesmethod.framework.models.java.projectmodel.JavaImport;
import org.parachutesmethod.framework.models.java.projectmodel.JavaMethod;

@JsonIgnoreProperties( {"preparedParachute", "parachuteMethodData", "pojos"})
public class BundleDescriptor {
    private String name;

    private JavaMethod parachuteMethodData;
    private CompilationUnit preparedParachute;
    private AnnotationsDescriptor parachuteAnnotations;
    private int retainedAnnotationsCount = 0;

    public BundleDescriptor(JavaMethod parachute) {
        name = parachute.getName();
        preparedParachute = new CompilationUnit();
        parachuteMethodData = parachute;
        parachute.getParachuteAnnotation()
                .ifPresent(javaAnnotation ->
                        parachuteAnnotations = new AnnotationsDescriptor(
                                javaAnnotation.getParameters()
                        )
                );

        preparedParachute.setPackageDeclaration("org.parachutesmethod.extractedparachutes");
        setImports(preparedParachute, parachuteMethodData.getParentFile().getImports());
        constructClassWithParachute();
    }

    private void setImports(CompilationUnit cu, List<JavaImport> javaImports) {
        NodeList<ImportDeclaration> imports = new NodeList<>();
        javaImports.forEach(i -> imports.add(i.getImportDeclaration()));
        cu.setImports(imports);
    }

    private void constructClassWithParachute() {
        ClassOrInterfaceDeclaration classDeclaration = preparedParachute.addClass(name);

        MethodDeclaration md = parachuteMethodData.getMethodDeclaration();
        if (Objects.nonNull(parachuteAnnotations)) {

            parachuteMethodData.getAnnotations().forEach(a -> {
                if (parachuteAnnotations.isParachuteAnnotationRetained()) {
                    if (a.isParachuteAnnotation()) {
                        md.addAnnotation(a.getAnnotationExpression());
                        retainedAnnotationsCount++;
                    }
                } else if (parachuteAnnotations.isOtherAnnotationRetained()) {
                    md.addAnnotation(a.getAnnotationExpression());
                    retainedAnnotationsCount++;
                }
            });
        }
        if (retainedAnnotationsCount == 0) {
            md.setAnnotations(new NodeList<>());
        }

        classDeclaration.getMembers().add(md);
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public CompilationUnit getPreparedParachute() {
        return preparedParachute;
    }

    public AnnotationsDescriptor getParachuteAnnotations() {
        return parachuteAnnotations;
    }
}
