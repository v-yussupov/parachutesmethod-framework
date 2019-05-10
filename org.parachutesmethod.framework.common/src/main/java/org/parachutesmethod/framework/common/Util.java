package org.parachutesmethod.framework.common;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

public class Util {

    public static void invokeShellCommand(Path directory, String command) {
        CommandLine commandLine = CommandLine.parse(command);
        System.out.println(commandLine.toString());

        Executor executor = new DefaultExecutor();
        executor.setWorkingDirectory(directory.toFile());
        executor.setStreamHandler(new PumpStreamHandler(System.out, System.err, System.in));

        CommandLine cl = new CommandLine("cmd");
        cl.addArguments("/c");
        cl.addArguments(command);

        try {
            executor.execute(cl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
