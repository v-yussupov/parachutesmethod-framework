package org.parachutesmethod.framework.models.java.parachutedescriptors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import org.parachutesmethod.framework.models.java.projectmodel.JavaImport;

import java.util.ArrayList;
import java.util.List;

public class BundleDescriptor {
    private String programmingLanguage;
    private String parachuteName;
    private String packageName;
    private String endpointPath;
    //private JavaMethod parachuteMethodData;

    private List<String> imports = new ArrayList<>();
    private AnnotationsDescriptor parachuteAnnotations;
    private String parachuteContainingClass;
    private List<ParachuteInputType> inputTypes = new ArrayList<>();
    private ParachuteOutputType outputType;
    private BuildScriptDescriptor buildScript;

    //private CompilationUnit preparedParachute;
    //private int retainedAnnotationsCount = 0;

    public BundleDescriptor() {
    }

    public BundleDescriptor(String programmingLanguage, String parachuteName, String packageName) {
        this.programmingLanguage = programmingLanguage;
        this.parachuteName = parachuteName;
        this.packageName = packageName;
    }

    private void prepareParachute() {
        /*preparedParachute = new CompilationUnit();
        parachuteMethodData.getParachuteAnnotation()
                .ifPresent(javaAnnotation ->
                        parachuteAnnotations = new AnnotationsDescriptor(
                                javaAnnotation.getParameters()
                        )
                );

        preparedParachute.setPackageDeclaration(JavaConfiguration.EXTRACTED_PARACHUTE_PACKAGE_NAME.value());
        setImports(preparedParachute, parachuteMethodData.getParentFile().getImports());
        constructClassWithParachute();*/
    }

    private void setImports(CompilationUnit cu, List<JavaImport> javaImports) {
        NodeList<ImportDeclaration> imports = new NodeList<>();
        javaImports.forEach(i -> imports.add(i.getImportDeclaration()));
        cu.setImports(imports);
    }

    private void constructClassWithParachute() {
        /*ClassOrInterfaceDeclaration classDeclaration = preparedParachute.addClass(parachuteName);

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

        classDeclaration.getMembers().add(md);*/
    }

    /*public CompilationUnit getPreparedParachute() {
        return preparedParachute;
    }*/

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getParachuteName() {
        return parachuteName;
    }

    public void setParachuteName(String parachuteName) {
        this.parachuteName = parachuteName;
    }

    public AnnotationsDescriptor getParachuteAnnotations() {
        return parachuteAnnotations;
    }

    public void setParachuteAnnotations(AnnotationsDescriptor parachuteAnnotations) {
        this.parachuteAnnotations = parachuteAnnotations;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public void addImport(String importDeclaration) {
        imports.add(importDeclaration);
    }

    public List<ParachuteInputType> getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(List<ParachuteInputType> inputTypes) {
        this.inputTypes = inputTypes;
    }

    public void addInputType(ParachuteInputType inputType) {
        inputTypes.add(inputType);
    }

    public ParachuteOutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(ParachuteOutputType outputType) {
        this.outputType = outputType;
    }

    public String getParachuteContainingClass() {
        return parachuteContainingClass;
    }

    public void setParachuteContainingClass(String parachuteContainingClass) {
        this.parachuteContainingClass = parachuteContainingClass;
    }

    public String getEndpointPath() {
        return endpointPath;
    }

    public void setEndpointPath(String endpointPath) {
        this.endpointPath = endpointPath;
    }

    public BuildScriptDescriptor getBuildScript() {
        return buildScript;
    }

    public void setBuildScript(BuildScriptDescriptor buildScript) {
        this.buildScript = buildScript;
    }

}
