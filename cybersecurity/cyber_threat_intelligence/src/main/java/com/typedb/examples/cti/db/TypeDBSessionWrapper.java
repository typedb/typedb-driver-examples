/*
 * Copyright (C) 2023 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.typedb.examples.cti.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBOptions;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typedb.client.api.answer.ConceptMap;
import com.vaticle.typedb.common.collection.Pair;
import com.typedb.examples.cti.configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TypeDBSessionWrapper {
    private final AppConfiguration appConfiguration;
    private final TypeDBClient client;
    private TypeDBSession session;

    @Autowired
    public TypeDBSessionWrapper(TypeDBClient client, AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        this.client = client;
        if (this.client.databases().contains(appConfiguration.getDatabase())) {
            session = this.client.session(appConfiguration.getDatabase(), TypeDBSession.Type.DATA,
                    TypeDBOptions.core().infer(true));
        }
    }

    private Pair<String, String> extractPair(ConceptMap values) {
        var json = values.toJSON().get("attribute");
        var key = json.asObject().get("type").asString();
        var valueTmp = json.asObject().get("value");
        String value;
        if (valueTmp.isString()) {
            value = valueTmp.asString();
        } else {
            value = valueTmp.toString();
        }
        return (new Pair<>(key, value));
    }

    public ObjectNode getAllJSON(String query) {
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {

            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {
                String key = e.owner().toJSON().toString().split("\"")[11];
                ObjectNode childNode = mapper.createObjectNode();
                e.conceptMaps().forEach(m -> {
                    var pair = extractPair(m);
                    childNode.put(pair.first(), pair.second());
                });
                rootNode.set(key, childNode);
            });
        }
        return rootNode;
    }

    public ObjectNode getIIDJSON(String query) {
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {

            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {
                String key = e.owner().asEntity().getIID();
                ObjectNode childNode = mapper.createObjectNode();
                e.conceptMaps().forEach(m -> {
                    var pair = extractPair(m);
                    childNode.put(pair.first(), pair.second());
                });
                rootNode.set(key, childNode);
            });
        }
        return rootNode;
    }

    public ObjectNode getSchemaJSON(String query) {
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {
            System.out.println("QUERY -> " + query);
            var dbResults = readTx.query().match(query);
            ArrayNode rel = mapper.createArrayNode();
            ArrayNode ent = mapper.createArrayNode();
            ArrayNode att = mapper.createArrayNode();

            dbResults.forEach(e -> {
                String key = e.map().toString().split(" ")[0];
                String value = e.map().toString().split(" ")[1];
                value = value.substring(0, value.length() - 2);
                if (key.charAt(3) == 'E') {
                    ent.add(value);
                }
                if (key.charAt(3) == 'A') {
                    att.add(value);
                }
                if (key.charAt(3) == 'R') {
                    rel.add(value);
                }

            });
            rootNode.set("Entities", ent);
            rootNode.set("Relations", rel);
            rootNode.set("Attributes", att);

        }
        return rootNode;
    }

    public ObjectNode getListJSON(String query, String relName, List<String> rolePlayers) {
        return getListJSON(query, relName, rolePlayers, false);
    }

    public ObjectNode getListJSON(String query, String relName, List<String> rolePlayers, boolean asAttribute) {
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {
            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {

                String key = e.owner().asRelation().getIID();
                ObjectNode childNode = mapper.createObjectNode();
                if (asAttribute) {
                    e.conceptMaps().forEach(m -> {
                        var pair = extractPair(m);
                        childNode.put(pair.first(), pair.second());
                    });
                }

                String[] rolePlayersTmp = rolePlayers.toArray(new String[0]);

                String queryBegin = "match $rel (";
                for (int i = 0; i < rolePlayersTmp.length; i++) {
                    queryBegin += rolePlayersTmp[i] + ": $r" + i + ",";
                }
                queryBegin = removeLastChar(queryBegin);
                queryBegin += ") isa " + relName + "; $rel iid " + key + ";";


                for (int i = 0; i < rolePlayersTmp.length; i++) {
                    String queryTmp = queryBegin;
                    queryTmp += "$r" + i + " has $attribute; $attribute isa! $i; group $r" + i + ";";
                    var nodeTmp = getIIDJSON(queryTmp);
                    childNode.set(removeFirstChar(rolePlayersTmp[i]), nodeTmp);
                }

                rootNode.set(key, childNode);
            });

        }
        return rootNode;
    }

    public ObjectNode getListAttrJSON(String query, String relName, List<String> rolePlayers, boolean asAttribute) {
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {
            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {

                String key = e.owner().asRelation().getIID();
                ObjectNode childNode = mapper.createObjectNode();
                if (asAttribute) {
                    e.conceptMaps().forEach(m -> {
                        var pair = extractPair(m);
                        childNode.put(pair.first(), pair.second());
                    });
                }

                String[] rolePlayersTmp = rolePlayers.toArray(new String[0]);

                String queryBegin = "match $rel (";
                for (int i = 0; i < rolePlayersTmp.length; i++) {
                    queryBegin += rolePlayersTmp[i] + ": $r" + i + ",";
                }
                queryBegin = removeLastChar(queryBegin);
                queryBegin += ") isa " + relName + "; $rel iid " + key + ";";


                for (int i = 0; i < rolePlayersTmp.length; i++) {
                    String queryTmp = queryBegin;
                    if (i != 0) {
                        queryTmp += "$r" + i + " has $attribute; $attribute isa! $i; group $r" + i + ";";
                        var nodeTmp = getIIDJSON(queryTmp);
                        childNode.set(removeFirstChar(rolePlayersTmp[i]), nodeTmp);
                    } else {
                        var dbResults2 = readTx.query().matchGroup(query);
                        dbResults2.forEach(w -> w.conceptMaps().forEach(m -> {
                            String value3 = m.map().get("rp1").asAttribute().toString();
                            value3 = removeLastChar(value3.split(":")[1]);
                            childNode.put(rolePlayersTmp[0], value3);

                        }));

                    }

                }

                rootNode.set(key, childNode);
            });

        }
        return rootNode;
    }

    public ObjectNode getRelJSON(String query, String relName, List<String> rolePlayers) {
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {
            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {

                String key = e.owner().toJSON().toString().split("\"")[11];
                ObjectNode childNode = mapper.createObjectNode();
                e.conceptMaps().forEach(m -> {
                    var pair = extractPair(m);
                    childNode.put(pair.first(), pair.second());
                });

                String[] rolePlayersTmp = rolePlayers.toArray(new String[0]);

                String queryBegin = "match $rel (";
                for (int i = 0; i < rolePlayersTmp.length; i++) {
                    queryBegin += rolePlayersTmp[i] + ": $r" + i + ",";
                }
                queryBegin = removeLastChar(queryBegin);
                queryBegin += ") isa " + relName + ", has stix_id \"" + key + "\";";
                for (int i = 0; i < rolePlayersTmp.length; i++) {
                    String queryTmp = queryBegin;
                    queryTmp += "$r" + i + " has $attribute, has stix_id $id; $attribute isa! $i; group $id;";
                    var nodeTmp = getAllJSON(queryTmp);
                    childNode.set(removeFirstChar(rolePlayersTmp[i]), nodeTmp);
                }

                rootNode.set(key, childNode);
            });

        }
        return rootNode;
    }

    public void newSession() {
        session = this.client.session(appConfiguration.getDatabase(), TypeDBSession.Type.DATA,
                TypeDBOptions.core().infer(true));
    }

    private String removeLastChar(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }

    private String removeFirstChar(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(1);
    }
}