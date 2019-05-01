package org.parachutesmethod.framework.models.java.parachutedescriptors;

import org.parachutesmethod.framework.common.BuildScript;

public class BuildScriptDescriptor {
    private String buildTool;
    private String buildScriptName;
    private String content;

    public BuildScriptDescriptor() {
    }

    public BuildScriptDescriptor(BuildScript buildScriptType, String content) {
        this.buildTool = buildScriptType.name();
        this.buildScriptName = buildScriptType.value();
        this.content = content;
    }

    public String getBuildScriptName() {
        return buildScriptName;
    }

    public void setBuildScriptName(String buildScriptName) {
        this.buildScriptName = buildScriptName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }
}