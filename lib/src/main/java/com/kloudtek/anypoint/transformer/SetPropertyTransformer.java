package com.kloudtek.anypoint.transformer;

import com.kloudtek.anypoint.Transformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SetPropertyTransformer implements Transformer {
    private String filename;
    private String key;
    private String value;
    private boolean applied;

    public SetPropertyTransformer() {
    }

    public SetPropertyTransformer(String filename, String key, String value) {
        this.filename = filename;
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean appliesTo(String filename) {
        return this.filename.equals(filename);
    }

    @Override
    public byte[] transform(String entryPath, @Nullable byte[] data) throws Exception {
        applied = true;
        Properties properties = new Properties();
        if (data != null) {
            try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
                properties.load(is);
            }
        }
        properties.setProperty(key, value);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        properties.store(os, "Updated by anypoint library");
        os.close();
        return os.toByteArray();
    }

    @NotNull
    @Override
    public List<String> getNewFiles() {
        return applied ? Collections.emptyList() : Collections.singletonList(filename);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
