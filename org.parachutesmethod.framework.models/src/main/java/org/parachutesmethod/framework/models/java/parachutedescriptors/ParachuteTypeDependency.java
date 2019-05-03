package org.parachutesmethod.framework.models.java.parachutedescriptors;

import java.util.ArrayList;
import java.util.List;

public class ParachuteTypeDependency {
    private boolean customType;
    private String typeName;
    private String typeBody;
    private List<String> imports = new ArrayList<>();
    private List<ParachuteTypeDependency> typeDependencies;

    public ParachuteTypeDependency() {

    }

    public ParachuteTypeDependency(boolean customType, String typeName) {
        this.customType = customType;
        this.typeName = typeName;
    }

    public boolean isCustomType() {
        return customType;
    }

    public void setCustomType(boolean customType) {
        this.customType = customType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeBody() {
        return typeBody;
    }

    public void setTypeBody(String typeBody) {
        this.typeBody = typeBody;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public void addImport(String i) {
        imports.add(i);
    }

    public List<ParachuteTypeDependency> getTypeDependencies() {
        return typeDependencies;
    }

    public void setTypeDependencies(List<ParachuteTypeDependency> typeDependencies) {
        this.typeDependencies = typeDependencies;
    }
}
