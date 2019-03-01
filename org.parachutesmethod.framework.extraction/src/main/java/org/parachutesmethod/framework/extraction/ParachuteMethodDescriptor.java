package org.parachutesmethod.framework.extraction;

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
public class ParachuteMethodDescriptor {
    private String parachuteName;
    private JavaMethod parachuteMethodData;
    private CompilationUnit preparedParachute;
    private ParachuteMethodAnnotationsDescriptor parachuteAnnotations;
    private int retainedAnnotationsCount = 0;

    ParachuteMethodDescriptor(JavaMethod parachute) {
        parachuteName = parachute.getName();
        preparedParachute = new CompilationUnit();
        parachuteMethodData = parachute;
        parachute.getParachuteAnnotation()
                .ifPresent(javaAnnotation ->
                        parachuteAnnotations = new ParachuteMethodAnnotationsDescriptor(
                                javaAnnotation.getParameters()
                        )
                );

        preparedParachute.setPackageDeclaration(Constants.EXTRACTED_PARACHUTE_PACKAGE_NAME);
        setImports(preparedParachute, parachuteMethodData.getParentFile().getImports());
        constructClassWithParachute();
    }

    private void setImports(CompilationUnit cu, List<JavaImport> javaImports) {
        NodeList<ImportDeclaration> imports = new NodeList<>();
        javaImports.forEach(i -> imports.add(i.getImportDeclaration()));
        cu.setImports(imports);
    }

    private void constructClassWithParachute() {
        ClassOrInterfaceDeclaration classDeclaration = preparedParachute.addClass(parachuteName);

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
    String getParachuteName() {
        return parachuteName;
    }

    CompilationUnit getPreparedParachute() {
        return preparedParachute;
    }

    public ParachuteMethodAnnotationsDescriptor getParachuteAnnotations() {
        return parachuteAnnotations;
    }
}
