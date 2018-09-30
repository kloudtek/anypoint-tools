package com.kloudtek.anypoint.runtime;

import java.io.File;

/**
 * Contains the logic to download, uncompress and configure a mule runtime
 */
public class RuntimeInstall {
    private String version;
    private File destDir;
    private File archive;

    public RuntimeInstall(String version, File destDir) {
        this.version = version;
        this.destDir = destDir;
    }

    public RuntimeInstall(String version, File destDir, File archive) {
        this.version = version;
        this.destDir = destDir;
        this.archive = archive;
    }

    public void install() {
        if (archive == null) {

        }
    }
}
