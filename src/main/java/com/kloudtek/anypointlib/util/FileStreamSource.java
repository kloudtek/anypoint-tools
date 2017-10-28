package com.kloudtek.anypointlib.util;

import java.io.*;

public class FileStreamSource implements StreamSource {
    private File file;

    public FileStreamSource(File file) {
        this.file = file;
        if (!file.exists()) {
            throw new IllegalArgumentException("File doesn't exist: " + file.exists());
        }
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public InputStream createInputStream() throws IOException {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
    }
}
