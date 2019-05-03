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

    private List<String> imports = new ArrayList<>();
    private AnnotationsDescriptor parachuteAnnotations;
    private String parachuteContainingClass;
    private String parqachuteMethodDeclaration;
    private List<ParachuteInputType> inputTypes = new ArrayList<>();
    private ParachuteReturnType returnType;
    private BuildScriptDescriptor buildScript;

    public BundleDescriptor() {
    }

    public BundleDescriptor(String programmingLanguage, String parachuteName, String packageName) {
        this.programmingLanguage = programmingLanguage;
        this.parachuteName = parachuteName;
        this.packageName = packageName;
    }

    private void setImports(CompilationUnit cu, List<JavaImport> javaImports) {
        NodeList<ImportDeclaration> imports = new NodeList<>();
        javaImports.forEach(i -> imports.add(i.getImportDeclaration()));
        cu.setImports(imports);
    }

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

    public ParachuteReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ParachuteReturnType returnType) {
        this.returnType = returnType;
    }

    public String getParachuteContainingClass() {
        return parachuteContainingClass;
    }

    public void setParachuteContainingClass(String parachuteContainingClass) {
        this.parachuteContainingClass = parachuteContainingClass;
    }

    public String getParqachuteMethodDeclaration() {
        return parqachuteMethodDeclaration;
    }

    public void setParqachuteMethodDeclaration(String parqachuteMethodDeclaration) {
        this.parqachuteMethodDeclaration = parqachuteMethodDeclaration;
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
