package org.parachutesmethod.framework.models.java.parachutedescriptors;

import org.parachutesmethod.framework.common.BuildScript;

public class BuildScriptDescriptor {
    private BuildScript buildScriptType;
    private String content;

    public BuildScriptDescriptor(BuildScript buildScriptType, String content) {
        this.buildScriptType = buildScriptType;
        this.content = content;
    }

    public BuildScript getBuildScriptType() {
        return buildScriptType;
    }

    public String getContent() {
        return content;
    }
}
