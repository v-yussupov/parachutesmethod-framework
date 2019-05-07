package org.parachutesmethod.framework.common;

public enum FileExtension {
    JSON(".json"),
    XML(".xml"),
    YAML(".yml"),
    JAVA(".java"),
    JAR(".jar"),
    WAR(".war");

    private final String extension;

    FileExtension(final String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }

}
