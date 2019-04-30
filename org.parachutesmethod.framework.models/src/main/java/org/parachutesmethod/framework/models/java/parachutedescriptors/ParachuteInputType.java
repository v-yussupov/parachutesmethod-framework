package org.parachutesmethod.framework.models.java.parachutedescriptors;

public class ParachuteInputType {
    private boolean isCustomType;
    private String typeName;
    private String typeBody;

    public ParachuteInputType(boolean isCustomType, String typeName) {
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
}
