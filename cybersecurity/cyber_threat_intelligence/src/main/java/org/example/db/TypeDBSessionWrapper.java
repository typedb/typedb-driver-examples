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

package org.example.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBOptions;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import org.example.Configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

public class TypeDBSessionWrapper {

    private final AppConfiguration appConfiguration;
    private static final Logger LOGGER = Logger.getLogger("TypeDBSessionWrapper");
    private final TypeDBClient client;
    private TypeDBSession session;

    public Hashtable<String, Hashtable<String, String>> getAll(String query) {
        newSession();
        Hashtable<String, Hashtable<String, String>> result = new Hashtable<String, Hashtable<String, String>>();

        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {

            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {
                String key = e.owner().asEntity().getIID();
                result.put(key, new Hashtable<String, String>());
                e.conceptMaps().forEach(m -> {
                    var json = m.toJSON().get("attribute").toString();
                    LOGGER.info(json);
                    var array = json.split(",");
                    String key2 = array[0].split("\":")[1];
                    String value2 = array[2].split("\":")[1].split("}")[0];
                    result.get(key).put(key2, value2);
                });
            });
        }

        return result;
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
                    var json = m.toJSON().get("attribute").toString();
                    var array = json.split(",");
                    String key2 = array[0].split("\":")[1];
                    String value2 = array[2].split("\":")[1].split("}")[0];
                    key2 = key2.replaceAll("^\"|\"$", "");
                    value2 = value2.replaceAll("^\"|\"$", "");
                    childNode.put(key2, value2);
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
                    var json = m.toJSON().get("attribute").toString();
                    var array = json.split(",");
                    String key2 = array[0].split("\":")[1];
                    String value2 = array[2].split("\":")[1].split("}")[0];
                    key2 = key2.replaceAll("^\"|\"$", "");
                    value2 = value2.replaceAll("^\"|\"$", "");
                    childNode.put(key2, value2);
                });
                rootNode.set(key, childNode);
            });
        }
        return rootNode;
    }

    public ObjectNode getSchemaJSON(String query){
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
                if(key.charAt(3) == 'E'){
                    ent.add(value);
                }
                if(key.charAt(3) == 'A'){
                    att.add(value);
                }
                if(key.charAt(3) == 'R'){
                    rel.add(value);
                }

            });
            rootNode.set("Entities", ent);
            rootNode.set("Relations", rel);
            rootNode.set("Attributes", att);

        }
        return rootNode;
    }


    public ObjectNode getListJSON(String query, String relName, String rolePlayers){
        return getListJSON(query, relName, rolePlayers, false);
    }

    public ObjectNode getListJSON(String query, String relName, String rolePlayers, boolean asAttribute){
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {
            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {

                String key = e.owner().asRelation().getIID();
                ObjectNode childNode = mapper.createObjectNode();
                if(asAttribute){
                    e.conceptMaps().forEach(m -> {
                        var json = m.toJSON().get("attribute").toString();

                        var array = json.split(",");
                        String key2 = array[0].split("\":")[1];
                        String value2 = array[2].split("\":")[1].split("}")[0];
                        key2 = key2.replaceAll("^\"|\"$", "");
                        value2 = value2.replaceAll("^\"|\"$", "");
                        childNode.put(key2, value2);
                    });
                }

                String[] rolePlayersTmp = rolePlayers.split(";");

                String queryBegin = "match $rel (";
                for(int i = 0; i < rolePlayersTmp.length; i++){
                    queryBegin += rolePlayersTmp[i] + ": $r" + i + ",";
                }
                queryBegin = removeLastChar(queryBegin);
                queryBegin += ") isa " + relName +"; $rel iid " + key + ";";


                for(int i = 0; i < rolePlayersTmp.length; i++){
                    String queryTmp = queryBegin;
                    queryTmp += "$r" + i + " has $attribute; $attribute isa! $i; group $r"+ i +";";
                    var nodeTmp = getIIDJSON(queryTmp);
                    childNode.set(removeFirstChar(rolePlayersTmp[i]), nodeTmp);
                }

                rootNode.set(key, childNode);
            });

        }
        return rootNode;
    }

    public ObjectNode getListAttrJSON(String query, String relName, String rolePlayers, boolean asAttribute){
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {
            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {

                String key = e.owner().asRelation().getIID();
                ObjectNode childNode = mapper.createObjectNode();
                if(asAttribute){
                    e.conceptMaps().forEach(m -> {
                        var json = m.toJSON().get("attribute").toString();

                        var array = json.split(",");
                        String key2 = array[0].split("\":")[1];
                        String value2 = array[2].split("\":")[1].split("}")[0];
                        key2 = key2.replaceAll("^\"|\"$", "");
                        value2 = value2.replaceAll("^\"|\"$", "");
                        childNode.put(key2, value2);
                    });
                }

                String[] rolePlayersTmp = rolePlayers.split(";");

                String queryBegin = "match $rel (";
                for(int i = 0; i < rolePlayersTmp.length; i++){
                    queryBegin += rolePlayersTmp[i] + ": $r" + i + ",";
                }
                queryBegin = removeLastChar(queryBegin);
                queryBegin += ") isa " + relName +"; $rel iid " + key + ";";


                for(int i = 0; i < rolePlayersTmp.length; i++){
                    String queryTmp = queryBegin;
                    if(i != 0){
                        queryTmp += "$r" + i + " has $attribute; $attribute isa! $i; group $r"+ i +";";
                        var nodeTmp = getIIDJSON(queryTmp);
                        childNode.set(removeFirstChar(rolePlayersTmp[i]), nodeTmp);
                    }
                    else{
                        queryTmp += " group $r"+ i +";";
                        var dbResults2 = readTx.query().matchGroup(query);
                        dbResults2.forEach(w -> {


                            ObjectNode childNode2 = mapper.createObjectNode();
                            w.conceptMaps().forEach(m -> {
                                String value3 = m.map().get("AAA").asAttribute().toString();
                                value3 = removeLastChar(value3.split(":")[1]);
                                childNode.put(rolePlayersTmp[0], value3);

                            });

                        });

                    }

                }

                rootNode.set(key, childNode);
            });

        }
        return rootNode;
    }

    public ObjectNode getRelJSON(String query, String relName, String rolePlayers){
        newSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {
            var dbResults = readTx.query().matchGroup(query);
            dbResults.forEach(e -> {

                String key = e.owner().toJSON().toString().split("\"")[11];
                ObjectNode childNode = mapper.createObjectNode();
                e.conceptMaps().forEach(m -> {
                    var json = m.toJSON().get("attribute").toString();

                    var array = json.split(",");
                    String key2 = array[0].split("\":")[1];
                    String value2 = array[2].split("\":")[1].split("}")[0];
                    key2 = key2.replaceAll("^\"|\"$", "");
                    value2 = value2.replaceAll("^\"|\"$", "");
                    childNode.put(key2, value2);
                });

                String[] rolePlayersTmp = rolePlayers.split(";");

                String queryBegin = "match $rel (";
                for(int i = 0; i < rolePlayersTmp.length; i++){
                     queryBegin += rolePlayersTmp[i] + ": $r" + i + ",";
                }
                queryBegin = removeLastChar(queryBegin);
                queryBegin += ") isa " + relName +", has stix_id \"" + key + "\";";
                for(int i = 0; i < rolePlayersTmp.length; i++){
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

    public void newSession(){
        session = this.client.session(appConfiguration.getDatabase(), TypeDBSession.Type.DATA,
                TypeDBOptions.core().infer(true));
    }

    public TypeDBSessionWrapper(TypeDBClient client, AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        this.client = client;
        if(this.client.databases().contains(appConfiguration.getDatabase())){
            session = this.client.session(appConfiguration.getDatabase(), TypeDBSession.Type.DATA,
                TypeDBOptions.core().infer(true));
        }
    }

    private String removeLastChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }

    private String removeFirstChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(1);
    }
}
