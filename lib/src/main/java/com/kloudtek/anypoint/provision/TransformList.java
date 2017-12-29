package com.kloudtek.anypoint.provision;

import com.kloudtek.util.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class TransformList {
    private Map<String, Transformer> transformers = new HashMap<>();

    public synchronized File applyTransforms(File appFile, File tmpDir) throws Exception {
        if (transformers.isEmpty()) {
            return appFile;
        }
        File tmpFile = File.createTempFile(appFile.getName(), "transformed.zip", tmpDir);
        ZipFile origZipFile = new ZipFile(appFile);
        try (FileOutputStream fout = new FileOutputStream(tmpFile); ZipOutputStream zout = new ZipOutputStream(fout)) {
            Enumeration<? extends ZipEntry> entries = origZipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                try (InputStream is = origZipFile.getInputStream(zipEntry)) {
                    String entryPath = zipEntry.getName();
                    zout.putNextEntry(new ZipEntry(entryPath));
                    Transformer transformer = transformers.get(entryPath);
                    if (transformer != null) {
                        zout.write(transformer.transform(IOUtils.toByteArray(is)));
                        transformers.remove(entryPath);
                    } else {
                        IOUtils.copy(is, zout);
                    }
                }
            }
            for (Map.Entry<String, Transformer> e : transformers.entrySet()) {
                zout.putNextEntry(new ZipEntry(e.getKey()));
                zout.write(e.getValue().transform(null));
            }
            return tmpFile;
        } catch (Exception e) {
            if (tmpFile.exists()) {
                if (!tmpFile.delete()) {
                    tmpFile.deleteOnExit();
                }
            }
            throw e;
        }
    }

    public synchronized void add(String path, Transformer transformer) {
        Transformer t = transformers.get(path);
        if (t != null) {
            t.addNext(transformer);
        } else {
            transformers.put(path, transformer);
        }
    }
}
