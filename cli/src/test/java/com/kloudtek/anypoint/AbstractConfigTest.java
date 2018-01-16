package com.kloudtek.anypoint;

import com.kloudtek.util.StringUtils;
import com.kloudtek.util.UserDisplayableException;
import com.kloudtek.util.io.IOUtils;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.IOException;

public class AbstractConfigTest {
    private File tmpConfig;

    public File createConfig() {
        try {
            if (tmpConfig != null) {
                throw new UserDisplayableException("Only one tmp config file supported");
            }
            File tmpDir = new File(".");
            File altTmpDir = new File(tmpDir, "build");
            if (altTmpDir.exists()) {
                tmpDir = altTmpDir;
            }
            tmpConfig = File.createTempFile("tmpconfig", ".json", tmpDir);
            if (tmpConfig.exists()) {
                if (!tmpConfig.delete()) {
                    throw new UserDisplayableException("Can't delete tmp file: " + tmpConfig.getPath());
                }
            }
            return tmpConfig;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File createConfig(String json) throws IOException {
        createConfig();
        IOUtils.write(tmpConfig, StringUtils.utf8(json));
        return tmpConfig;
    }

    @AfterEach
    public void deleteTmpConfig() {
        if (tmpConfig != null && tmpConfig.exists()) {
            if (!tmpConfig.delete()) {
                tmpConfig.deleteOnExit();
            }
        }
    }
}
