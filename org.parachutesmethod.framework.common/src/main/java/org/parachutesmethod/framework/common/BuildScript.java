package org.parachutesmethod.framework.common;

public enum BuildScript {
    MAVEN("pom.xml"),
    GRADLE("gradle.build");

    private String value;

    BuildScript(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
