package org.parachutesmethod.framework.extraction.languages;

import org.parachutesmethod.framework.extraction.exceptions.LangSupportException;

public enum SupportedLanguage {
    JAVA("java", ".java"),
    PYTHON("python", ".py");

    private final String name;
    private final String fileExtension;

    SupportedLanguage(final String name, final String fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public String getName() {
        return name;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static SupportedLanguage getValue(String test) throws LangSupportException {
        if (test == null) {
            throw new NullPointerException("alias cannot be null");
        }
        for (SupportedLanguage lang : SupportedLanguage.values()) {
            if (lang.getName().equals(test.toLowerCase())) {
                return lang;
            }
        }

        throw new LangSupportException("The language " + test + " is not supported");
    }

}
