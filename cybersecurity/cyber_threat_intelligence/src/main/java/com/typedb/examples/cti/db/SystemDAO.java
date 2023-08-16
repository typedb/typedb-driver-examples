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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.model.domain.object.identity.System;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SystemDAO {
    protected static final String SYSTEM_MATCH =
            "  $system isa system, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";
    private final TypeDBSessionWrapper db;
    private final List<String> typeString;
    private final ObjectMapper objectMapper;


    public SystemDAO(TypeDBSessionWrapper db) {
        this.db = db;
        typeString = System.typeString;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private ObjectNode find(String getQueryStr) {
        return db.getAllJSON(getQueryStr);
    }

    public ObjectNode findAll() {
        var getQueryStr = "match " + SYSTEM_MATCH + "group $id; ";
        return find(getQueryStr);
    }

    public Set<System> findAllBeans() throws JsonProcessingException {
        ObjectNode json = findAll();
        Map<String, System> result= objectMapper.readValue(json.toString(), new TypeReference<>() {
        });

        return new HashSet<>(result.values());
    }

    public ObjectNode search(String attrType, String attrName) {

        if (typeString.contains(attrType)) {
            attrName = "\"" + attrName + "\"";
        }

        String search = "$system has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + SYSTEM_MATCH + search + "group $id;";

        return find(getQueryStr);
    }

    public Set<System> searchBeans(String attrType, String attrName) throws JsonProcessingException {
        ObjectNode json = search(attrType, attrName);
        Map<String, System> result= objectMapper.readValue(json.toString(), new TypeReference<>() {
        });

        return new HashSet<>(result.values());
    }


}


