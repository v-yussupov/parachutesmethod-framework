package org.parachutesmethod.framework.models.java.parachutedescriptors;

import java.util.List;

public class ParachuteTypeDependency {
    private boolean isCustomType;
    private String typeName;
    private String typeBody;
    private List<ParachuteTypeDependency> typeDependencies;

    public ParachuteTypeDependency(boolean isCustomType, String typeName) {
        this.isCustomType = isCustomType;
        this.typeName = typeName;
    }


    public boolean isCustomType() {
        return isCustomType;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTypeBody() {
        return typeBody;
    }

    public void setTypeBody(String typeBody) {
        this.typeBody = typeBody;
    }

    public List<ParachuteTypeDependency> getTypeDependencies() {
        return typeDependencies;
    }

    public void setTypeDependencies(List<ParachuteTypeDependency> typeDependencies) {
        this.typeDependencies = typeDependencies;
    }
}
