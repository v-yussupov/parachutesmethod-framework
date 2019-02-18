package org.parachutesmethod.framework.extraction;

import java.io.IOException;

public interface RemoteLinkProcessor {

    boolean downloadGitHubRepository(String url) throws IOException;
}
