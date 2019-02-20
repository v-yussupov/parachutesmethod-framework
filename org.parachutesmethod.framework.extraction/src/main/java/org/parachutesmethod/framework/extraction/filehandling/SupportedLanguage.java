package org.parachutesmethod.framework.extraction.filehandling;

public enum SupportedLanguage {
    JAVA("java", "*.java"),
    PYTHON("python", "*.py");

    private final String name;
    private final String globbingPattern;

    SupportedLanguage(final String name, final String globbingPattern) {
        this.name = name;
        this.globbingPattern = globbingPattern;
    }

    String getName() {
        return name;
    }

    String getGlobbingPattern() {
        return globbingPattern;
    }

    public static SupportedLanguage getValue(String test) {
        if (test == null) {
            throw new NullPointerException("alias cannot be null");
        }
        for (SupportedLanguage lang : SupportedLanguage.values()) {
            if (lang.getName().equals(test.toLowerCase())) {
                return lang;
            }
        }

        throw new IllegalArgumentException("The language " + test + " is not supported");
    }

}
