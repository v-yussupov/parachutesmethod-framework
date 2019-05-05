package org.parachutesmethod.framework.models.java;

public enum JavaConfiguration {
    PARACHUTE_METHOD_ANNOTATION("ParachuteMethod"),
    FILE_WITHOUT_PACKAGE("notpackaged"),
    PATH_ANNOTATION("Path"),
    JAVA_PROJECT_FILES_PATH("src/main/java"),
    PARACHUTE_PACKAGE("org.parachutes");

    private String value;

    JavaConfiguration(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
