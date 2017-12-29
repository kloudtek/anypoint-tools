package com.kloudtek.anypoint.provision.transformer;

import com.kloudtek.anypoint.provision.Transformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class SetPropertyTransformer extends Transformer {
    private String key;
    private String value;

    public SetPropertyTransformer() {
    }

    public SetPropertyTransformer(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public byte[] doTransform(byte[] data) throws Exception {
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
