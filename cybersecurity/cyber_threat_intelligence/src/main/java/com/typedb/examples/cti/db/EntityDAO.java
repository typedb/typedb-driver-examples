package com.typedb.examples.cti.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityDAO<T> {
    protected static final String ENT_MATCH =
            "$%s isa %s, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";

    private final String nameEnt;
    private final TypeDBSessionWrapper db;
    private final List<String> typeString;
    private final ObjectMapper objectMapper;

    public EntityDAO(TypeDBSessionWrapper db, String nameEnt, List<String> typeString) {
        this.db = db;
        this.nameEnt = nameEnt;
        this.typeString = typeString;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private ObjectNode find(String getQueryStr) {
        return db.getAllJSON(getQueryStr);
    }

    public ObjectNode findAll() {
        String query = String.format(ENT_MATCH, nameEnt, nameEnt);
        var getQueryStr = "match " + query + "group $id; ";
        return find(getQueryStr);
    }

    public Set<T> findAllBeans() throws JsonProcessingException {
        ObjectNode json = findAll();
        Map<String, T> result= objectMapper.readValue(json.toString(), new TypeReference<>() {});

        return new HashSet<>(result.values());
    }

    public ObjectNode search(String attrType, String attrName) {

        if (typeString.contains(attrType)) {
            attrName = "\"" + attrName + "\"";
        }
        String query = String.format(ENT_MATCH, nameEnt, nameEnt);
        String search = "$" + nameEnt + " has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + query + search + "group $id;";

        return find(getQueryStr);
    }

    public Set<T> searchBeans(String attrType, String attrName) throws JsonProcessingException {
        ObjectNode json = search(attrType, attrName);
        Map<String, T> result= objectMapper.readValue(json.toString(), new TypeReference<>() {});

        return new HashSet<>(result.values());
    }


}
