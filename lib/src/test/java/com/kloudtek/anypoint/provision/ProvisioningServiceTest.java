package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.anypoint.TransformList;
import com.kloudtek.util.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProvisioningServiceTest {
    private final File tmpDir;

    public ProvisioningServiceTest() throws IOException {
        tmpDir = new File("target/tmp");
        if (!tmpDir.exists()) {
            if (!tmpDir.mkdirs()) {
                throw new IOException("Unable to create " + tmpDir.getPath());
            }
        }
    }

    @Test
    public void testExpressionLanguageSimple() {
        ProvisioningServiceImpl s = new ProvisioningServiceImpl();
        HashMap<String, String> p = new HashMap<>();
        p.put("world", "Glarg");
        p.put("foo", "bar");
        assertEquals("helloGlarg", AnypointClient.parseEL("hello${world}", p));
        assertEquals("helloGlargXbar", AnypointClient.parseEL("hello${world}X${foo}", p));
        assertEquals("hello${world}", AnypointClient.parseEL("hello$${world}", p));
        assertEquals("hello$world}", AnypointClient.parseEL("hello$world}", p));
    }

    @Test
    public void testUpdatePropertyFileNewFile() throws Exception {
        testUpdatePropertyFileImpl("nprop", "classes/configs.properties");
    }

    @Test
    public void testUpdatePropertyFileExistingFile() throws Exception {
        Properties p = testUpdatePropertyFileImpl("eprop", "classes/cfg.properties");
        Assertions.assertEquals("bar", p.getProperty("foo"));
    }

    private Properties testUpdatePropertyFileImpl(String type, String propertyFile) throws Exception {
        MockedAnypointClient client = new MockedAnypointClient();
        File appFile = createApp(type);
        File newZipFile = null;
        try {
            Organization org = client.setupDefaultMocks();
            HashMap<String, String> params = new HashMap<>();
            params.put("url", "http://www.moo.com");
            ProvisioningConfig cfg = new ProvisioningConfig(params, null);
            TransformList transformList = client.provision(org, appFile, cfg, "sit");
            newZipFile = transformList.applyTransforms(appFile, tmpDir);
            Properties p = getPropertyFile(newZipFile, propertyFile);
            Assertions.assertEquals("myclientid", p.getProperty("mule.client.id"));
            Assertions.assertEquals("myclientsecret", p.getProperty("mule.client.secret"));
            return p;
        } finally {
            deleteApp(appFile);
            deleteApp(newZipFile);
        }
    }

    private Properties getPropertyFile(File file, String path) throws IOException {
        try (ZipFile zipFile = new ZipFile(file)) {
            ZipEntry entry = zipFile.getEntry(path);
            if (entry == null) {
                throw new IllegalStateException("zip file " + file.getPath() + " missing property file " + path);
            } else {
                Properties p = new Properties();
                try (InputStream is = zipFile.getInputStream(entry)) {
                    p.load(is);
                }
                return p;
            }
        }
    }

    private void deleteApp(File file) {
        if (file != null && file.exists() && !file.delete()) {
            file.deleteOnExit();
        }
    }

    private File createApp(String type) throws IOException, URISyntaxException {
        File file = File.createTempFile("app", type + ".zip", tmpDir);
        if (!file.delete()) {
            throw new IOException("Unable to delete tmp file: " + file.getPath());
        }
        File appDir = new File(getClass().getResource("/app").toURI());
        try (FileOutputStream fout = new FileOutputStream(file); ZipOutputStream zout = new ZipOutputStream(fout)) {
            addToApp(zout, appDir, "mule-deploy.properties");
            addToApp(zout, appDir, "mule-app.properties");
            addToApp(zout, appDir, "deletemeapp.xml");
            addToApp(zout, appDir, "classes/log4j2.xml");
            addToApp(zout, appDir, "classes/cfg.properties");
            addToApp(zout, appDir, "anypoint-" + type + ".json", "anypoint.json");
        }
        file.deleteOnExit();
        return file;
    }

    private void addToApp(ZipOutputStream zout, File appDir, String path) throws IOException {
        addToApp(zout, appDir, path, path);
    }

    private void addToApp(ZipOutputStream zout, File appDir, String path, String zipPath) throws IOException {
        File file = new File(appDir + File.separator + path.replace("/", File.separator));
        ZipEntry ze = new ZipEntry(zipPath);
        zout.putNextEntry(ze);
        zout.write(IOUtils.toByteArray(file));
    }
}
