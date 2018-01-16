package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kloudtek.anypoint.*;
import com.kloudtek.util.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ProvisioningServiceImpl implements ProvisioningService {
    private static final Logger logger = LoggerFactory.getLogger(ProvisioningServiceImpl.class);

    @Override
    public TransformList provision(AnypointClient client, Organization org, File file, ProvisioningConfig provisioningConfig, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry entry = zipFile.getEntry(provisioningConfig.getDescriptorLocation());
        if (entry != null) {
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                ProvisioningDescriptor provisioningDescriptor = new ProvisioningDescriptor(this, zipFile, provisioningConfig, envSuffix);
                String json = IOUtils.toString(inputStream);
                try {
                    if (provisioningConfig.isLegacyMode()) {
                        json = convertLegacyDescriptor(client.getJsonHelper().getJsonMapper(), json);
                    }
                    provisioningDescriptor = client.getJsonHelper().readJson(provisioningDescriptor, json);
                    provisioningDescriptor.validate();
                    provisioningDescriptor.provision(org);
                    return provisioningDescriptor.getTransformList();
                } catch (InvalidJsonException e) {
                    throw new InvalidAnypointDescriptorException(e.getMessage(), e);
                }
            }
        }
        return new TransformList();
    }

    public static String convertLegacyDescriptor(ObjectMapper jsonMapper, String json) throws IOException {
        ObjectNode tree = (ObjectNode) jsonMapper.readTree(json);
        ArrayNode apis = (ArrayNode) tree.get("provided-apis");
        if (apis != null && apis.size() > 0) {
            tree.remove("provided-apis");
            tree.putArray("apis").addAll(apis);
            Iterator<JsonNode> i = apis.iterator();
            while (i.hasNext()) {
                ObjectNode api = (ObjectNode) i.next();
                convertVersionToLegacy(api);
                String apiName = api.get("name").asText();
                api.put("endpoint", "http://api/" + apiName);
                ArrayNode policies = api.putArray("policies");
                ObjectNode policy = policies.addObject();
                policy.put("type", "client-id-enforcement");
                String prefix = apiName.endsWith("exp-api") ? "mule_" : "";
                policy.put("clientIdExpr", "#[message.inboundProperties[\\\"" + prefix + "client_id\\\"]]");
                policy.put("clientSecretExpr", "#[message.inboundProperties[\\\"" + prefix + "client_secret\\\"]]");
                api.put("addCredsToPropertyFile", "classes/config.properties");
                ArrayNode access = (ArrayNode) api.get("access");
                if (access != null) {
                    for (JsonNode jsonNode : access) {
                        convertVersionToLegacy(jsonNode);
                    }
                }
            }
        } else {
            tree.putArray("apis");
        }
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
    }

    @Nullable
    private static JsonNode convertVersionToLegacy(JsonNode api) {
        JsonNode version = api.get("version");
        if (version != null) {
            ((ObjectNode) api).put("version", "v" + version.asText());
        }
        return version;
    }
}
