package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.util.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProvisioningServiceImplTest {
    @Test
    void convertLegacyDescriptorExpApi() throws IOException {
        testConvertLegacy("/legacyAnypoint-cfa.json", "/legacyAnypoint-cfa-expected.json");
    }

    @Test
    void convertLegacyDescriptorNonExpApi() throws IOException {
        testConvertLegacy("/legacyAnypoint-prc.json", "/legacyAnypoint-prc-expected.json");
    }

    private void testConvertLegacy(String jsonFilename, String expectedJsonFilename) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = IOUtils.toString(getClass().getResourceAsStream(jsonFilename));
        String expectedJson = IOUtils.toString(getClass().getResourceAsStream(expectedJsonFilename));
        String convertedJson = ProvisioningServiceImpl.convertLegacyDescriptor(jsonMapper, json);
        TypeReference<Map<String, Object>> mapTypeRef = new TypeReference<Map<String, Object>>() {
        };
        Map convertedJsonMap = jsonMapper.readValue(convertedJson, mapTypeRef);
        Map expectedMap = jsonMapper.readValue(expectedJson, mapTypeRef);
        assertEquals(expectedMap, convertedJsonMap);
    }

}