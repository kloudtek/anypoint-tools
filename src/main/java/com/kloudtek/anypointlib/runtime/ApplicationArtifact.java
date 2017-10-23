package com.kloudtek.anypointlib.runtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationArtifact {
    private int id;
    private String name;
    private String fileName;
    private String fileChecksum;

    public ApplicationArtifact() {
    }

    public ApplicationArtifact(int id, String name, String fileName, String fileChecksum) {
        this.id = id;
        this.name = name;
        this.fileName = fileName;
        this.fileChecksum = fileChecksum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileChecksum() {
        return fileChecksum;
    }

    public void setFileChecksum(String fileChecksum) {
        this.fileChecksum = fileChecksum;
    }
}
