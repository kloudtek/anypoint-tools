package com.kloudtek.anypoint.util;

import java.io.IOException;
import java.io.InputStream;

public class ClassPathStreamSource implements StreamSource {
    private String filename;
    private Class<?> classSource;
    private String path;

    public ClassPathStreamSource(Class<?> classSource, String path) {
        this(classSource, path.contains("/") ? path.substring(path.indexOf("/") + 1, path.length()) : path, path);
    }

    public ClassPathStreamSource(Class<?> classSource, String filename, String path) {
        this.classSource = classSource;
        this.filename = filename;
        this.path = path;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public InputStream createInputStream() throws IOException {
        return getClass().getResourceAsStream(path);
    }
}
