package org.parachutesmethod.framework.extraction.explorers.java.model;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.nio.file.Path;
import java.util.List;

public class MavenProjectObjectModel {
    private Path location;
    private Model pom;

    public MavenProjectObjectModel(Path location, Model pom) {
        this.location = location;
        this.pom = pom;
    }

    public Path getLocation() {
        return location;
    }

    public Model getPom() {
        return pom;
    }

    public List<Dependency> getDependencies() {
        return pom.getDependencies();
    }
}
