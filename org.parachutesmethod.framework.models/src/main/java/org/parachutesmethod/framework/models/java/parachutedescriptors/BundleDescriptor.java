package org.parachutesmethod.framework.models.java.parachutedescriptors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.parachutesmethod.framework.models.java.JavaConfiguration;
import org.parachutesmethod.framework.models.java.projectmodel.JavaImport;
import org.parachutesmethod.framework.models.java.projectmodel.JavaMethod;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties({"preparedParachute", "parachuteMethodData"})
public class BundleDescriptor {
    private String parachuteName;
    private String packageName;
    private JavaMethod parachuteMethodData;

    private List<String> imports;
    private AnnotationsDescriptor parachuteAnnotations;
    private String methodBody;
    private ParachuteInputType input;
    private ParachuteOutputType output;
    private BuildScriptDescriptor buildScript;
    private String endpointPath;

    private CompilationUnit preparedParachute;
    private int retainedAnnotationsCount = 0;

    public BundleDescriptor(String parachuteName, String packageName) {
        this.parachuteName = parachuteName;
        this.packageName = packageName;

        //parachuteMethodData = parachute;
    }

    private void prepareParachute() {
        preparedParachute = new CompilationUnit();
        parachuteMethodData.getParachuteAnnotation()
                .ifPresent(javaAnnotation ->
                        parachuteAnnotations = new AnnotationsDescriptor(
                                javaAnnotation.getParameters()
                        )
                );

        preparedParachute.setPackageDeclaration(JavaConfiguration.EXTRACTED_PARACHUTE_PACKAGE_NAME.value());
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
    public String getParachuteName() {
        return parachuteName;
    }

    public CompilationUnit getPreparedParachute() {
        return preparedParachute;
    }

    public AnnotationsDescriptor getParachuteAnnotations() {
        return parachuteAnnotations;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }
}
