package org.parachutesmethod.framework.extraction.languages;

import org.parachutesmethod.framework.extraction.exceptions.LangSupportException;

public enum SupportedLanguage {
    JAVA("java"),
    PYTHON("python");

    private final String name;

    SupportedLanguage(final String name) {
        this.name = name;
    }

    public static SupportedLanguage getValue(String test) throws LangSupportException {
        if (test == null) {
            throw new LangSupportException("alias cannot be null");
        }
        for (SupportedLanguage lang : SupportedLanguage.values()) {
            if (lang.getName().equals(test.toLowerCase())) {
                return lang;
            }
        }

        throw new LangSupportException("The language " + test + " is not supported");
    }

    public String getName() {
        return name;
    }
}
