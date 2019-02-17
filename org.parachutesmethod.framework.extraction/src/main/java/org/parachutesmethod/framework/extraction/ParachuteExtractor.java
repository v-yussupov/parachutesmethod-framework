package org.parachutesmethod.framework.extraction;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.nio.file.Paths;

public class ParachuteExtractor implements RemoteLinkProcessor {

    public ParachuteExtractor() {

    }

    @Override
    public boolean downloadGitHubRepository(String url) {

        String cloneDirectoryPath = "C:\\Temp\\parachutes-projects";
        try {
            System.out.println("Cloning " + url + " into " + cloneDirectoryPath);
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(Paths.get(cloneDirectoryPath).toFile())
                    .call();
            System.out.println("Completed Cloning");
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
        return false;
    }
}
