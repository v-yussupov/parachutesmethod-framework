package org.parachutesmethod.framework.extraction;

public enum ExtractionSetting {
    SOURCE_PROJECT_FOLDER("source"),
    GENERATION_BUNDLES_FOLDER("parachute-generation-bundles"),
    BUNDLE_SPECFILE_NAME("parachute-descriptor");

    private String value;

    ExtractionSetting(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
