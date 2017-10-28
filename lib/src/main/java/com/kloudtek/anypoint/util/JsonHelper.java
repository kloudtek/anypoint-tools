package com.kloudtek.anypoint.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.InvalidJsonException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonHelper {
    private ObjectMapper jsonMapper = new ObjectMapper();
    private AnypointClient client;

    public JsonHelper(AnypointClient client) {
        this.client = client;
    }

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    public byte[] toJson(Object obj) {
        try (ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
            jsonMapper.writeValue(tmp, obj);
            return tmp.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> toJsonMap(String json) {
        try {
            return jsonMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    public Map<String, Object> toJsonMap(JsonNode node) {
        try {
            return jsonMapper.treeToValue(node,Map.class);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e);
        }
    }

    public MapBuilder buildJsonMap() {
        return new MapBuilder();
    }

    public MapBuilder buildJsonMap(Map<String, Object> data) {
        return new MapBuilder(null,data);
    }

    public JsonNode readJsonTree(String json) {
        try {
            return jsonMapper.readTree(json);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    public <X> X readJson(X obj, String json, String jsonPath) {
        try {
            return readJson(obj, jsonMapper.readerForUpdating(obj).readTree(json).at(jsonPath));
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    public <X> X readJson(X obj, String json) {
        try {
            jsonMapper.readerForUpdating(obj).readValue(json);
            if (obj instanceof AnypointObject) {
                ((AnypointObject) obj).setJson(json);
            }
            return obj;
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <X> X readJson(Class<?> objClass, JsonNode node, AnypointObject<?> parent) {
        try {
            Object obj = jsonMapper.treeToValue(node, objClass);
            if (obj instanceof AnypointObject) {
                ((AnypointObject) obj).setJson(node.toString());
                ((AnypointObject) obj).setParent(parent);
            }
            return (X) obj;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <X> List<X> readJsonList(Class<X> objClass, String json, AnypointObject<?> parent) {
        return readJsonList(objClass,json,parent,null);
    }

    @SuppressWarnings("unchecked")
    public <X> List<X> readJsonList(Class<X> objClass, String json, AnypointObject<?> parent, String path) {
        try {
            ArrayList<X> list = new ArrayList<>();
            JsonNode node = jsonMapper.readTree(json);
            if( path != null ) {
                node = node.at(path);
            }
            for (JsonNode n : node) {
                list.add(readJson(objClass, n, parent));
            }
            return list;
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    public <X> X readJson(X obj, JsonNode node) {
        try {
            jsonMapper.readerForUpdating(obj).readValue(node);
            if (obj instanceof AnypointObject) {
                ((AnypointObject) obj).setJson(node.toString());
                ((AnypointObject) obj).setClient(client);
            }
            return obj;
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    public class MapBuilder {
        private MapBuilder parent;
        private Map<String, Object> request;

        public MapBuilder() {
            request = new HashMap<>();
        }

        public MapBuilder(MapBuilder parent, Map<String, Object> request) {
            this.parent = parent;
            this.request = request;
        }

        public MapBuilder set(String key, Object value) {
            request.put(key, value);
            return this;
        }

        public MapBuilder setNested(String nestKey, String key, Object value) {
            HashMap<String, Object> nestedMap = new HashMap<>();
            nestedMap.put(key, value);
            request.put(nestKey, nestedMap);
            return this;
        }

        public Map<String, Object> toMap() {
            if (parent != null) {
                return parent.toMap();
            } else {
                return request;
            }
        }

        public MapBuilder addMap(String name) {
            HashMap<String, Object> subMap = new HashMap<>();
            request.put(name, subMap);
            return new MapBuilder(this, subMap);
        }
    }
}
