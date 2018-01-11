package com.kloudtek.anypoint.provision;

import com.kloudtek.util.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class TransformList extends ArrayList<Transformer> {
    public synchronized File applyTransforms(File appFile, File tmpDir) throws Exception {
        if (isEmpty()) {
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
                    if(isTransformable(entryPath)) {
                        byte[] data = IOUtils.toByteArray(is);
                        for (Transformer transformer : this) {
                            if( transformer.appliesTo(entryPath) ) {
                                data = transformer.transform(entryPath,data);
                            }
                        }
                        zout.write(data);
                    } else {
                        IOUtils.copy(is, zout);
                    }
                }
            }
            HashSet<String> newFiles = new HashSet<>();
            for (Transformer transformer : this) {
                newFiles.addAll(transformer.getNewFiles());
            }
            for (String newFile : newFiles) {
                zout.putNextEntry(new ZipEntry(newFile));
                byte[] data = null;
                for (Transformer transformer : this) {
                    if( transformer.appliesTo(newFile) ) {
                        data = transformer.transform(newFile,data);
                    }
                }
                if(data != null) {
                    zout.write(data);
                }
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

    private boolean isTransformable(String entryPath) {
        for (Transformer transformer : this) {
            if( transformer.appliesTo(entryPath) ) {
                return true;
            }
        }
        return false;
    }
}
