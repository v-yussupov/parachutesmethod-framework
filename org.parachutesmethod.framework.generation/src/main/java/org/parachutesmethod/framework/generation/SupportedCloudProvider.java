package org.parachutesmethod.framework.generation;

import java.util.Collections;
import java.util.List;

import org.parachutesmethod.framework.extraction.languages.SupportedLanguage;

public enum SupportedCloudProvider {
    AWS("aws", Collections.singletonList(SupportedLanguage.JAVA)),
    MSA("msazure", Collections.EMPTY_LIST);

    private final String name;
    private final List<SupportedLanguage> supportedLanguages;

    SupportedCloudProvider(final String name, List<SupportedLanguage> supportedLanguages) {
        this.name = name;
        this.supportedLanguages = supportedLanguages;
    }

    public String getName() {
        return name;
    }

    public static SupportedCloudProvider getValue(String test) {
        if (test == null) {
            throw new NullPointerException("alias cannot be null");
        }
        for (SupportedCloudProvider p : SupportedCloudProvider.values()) {
            if (p.getName().equalsIgnoreCase(test)) {
                return p;
            }
        }

        throw new IllegalArgumentException("The language " + test + " is not supported");
    }

    public List<SupportedLanguage> getSupportedLanguages() {
        return supportedLanguages;
    }

    public SupportedLanguage checkSupport(String projectLanguage) {
        for (SupportedLanguage lang : supportedLanguages) {
            if (lang.getName().equalsIgnoreCase(projectLanguage)) {
                return lang;
            }
        }
        return null;
    }
}
