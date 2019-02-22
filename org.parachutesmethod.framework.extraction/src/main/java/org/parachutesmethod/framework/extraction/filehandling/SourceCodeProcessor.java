package org.parachutesmethod.framework.extraction.filehandling;

import java.nio.file.Path;

public interface SourceCodeProcessor {
    void handle(Path path);
}
