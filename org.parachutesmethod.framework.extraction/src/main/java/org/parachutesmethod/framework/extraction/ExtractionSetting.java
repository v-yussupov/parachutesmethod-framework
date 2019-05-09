package org.parachutesmethod.framework.extraction;

public enum ExtractionSetting {
    SOURCE_PROJECT_FOLDER("source"),
    DESCRIPTORS_FOLDER_NAME("parachute-descriptors"),
    DESCRIPTOR_NAME("descriptor");

    private String value;

    ExtractionSetting(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
