package com.kloudtek.anypoint.ocli;

import java.io.File;

/**
 * This class allows to use the official anypoint cli tool
 */
public class OCliClient {
    private final String executableOnPath;

    /**
     * @throws IllegalStateException if anypoint cli is not found in path
     */
    public OCliClient() {
        executableOnPath = findExecutableOnPath("anypoint-cli");
    }

    public OCliClient(String executableOnPath) {
        this.executableOnPath = executableOnPath;
    }

    public static String findExecutableOnPath(String name) {
        for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
            File file = new File(dirname, name);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        throw new IllegalStateException("anypoint-cli executable not found in path");
    }

}
