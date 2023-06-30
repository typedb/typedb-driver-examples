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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.model.KillChainPhase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KillChainPhaseDAO {
    TypeDBSessionWrapper db;
    String typeString;

    protected static final String KILL_CHAIN_PHASE_MATCH =
            "  $kill_chain_phase isa kill_chain_phase, has kill_chain_name $id, has $attribute;" +
                    "$attribute isa! $j; ";

    public KillChainPhaseDAO(TypeDBSessionWrapper db) {
        this.db = db;
        KillChainPhase tempKillChainPhase = new KillChainPhase();
        typeString = tempKillChainPhase.getTypeString();
    }

    private ObjectNode getJSON(String getQueryStr) {
        return db.getAllJSON(getQueryStr);
    }

    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + KILL_CHAIN_PHASE_MATCH + "group $id; ";
        return getJSON(getQueryStr);
    }

    public String getAllString() {
        return getAllJSON().toString();
    }

    public Set<KillChainPhase> getAllBeans() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String getQueryStr = "match " + KILL_CHAIN_PHASE_MATCH + "group $id;";
        ObjectNode json = getJSON(getQueryStr);
        Map<String, KillChainPhase> test = objectMapper.readValue(json.toString(), new TypeReference<Map<String, KillChainPhase>>(){});
        Set<KillChainPhase> result = new HashSet<>(test.values());

        return result;
    }

    public ObjectNode getSearchJSON(String attrType, String attrName) {

        if (typeString.contains(" " + attrType + ";")){
            attrName = "\"" + attrName + "\"";
        }

        String search = "$kill_chain_phase has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + KILL_CHAIN_PHASE_MATCH + search + "group $id;";

        return getJSON(getQueryStr);
    }

    public String getSearchString(String attrType, String attrName) {
        return getSearchJSON(attrType, attrName).toString();
    }

    public Set<KillChainPhase> getSearchBeans(String attrType, String attrName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (typeString.contains(" " + attrType + ";")){
            attrName = "\"" + attrName + "\"";
        }

        String search = "$kill_chain_phase has " + attrType + " = " + attrName + ";";

        String getQueryStr = "match " + KILL_CHAIN_PHASE_MATCH + search + " group $id;";
        ObjectNode json = getJSON(getQueryStr);
        Map<String, KillChainPhase> test = objectMapper.readValue(json.toString(), new TypeReference<Map<String, KillChainPhase>>(){});
        Set<KillChainPhase> result = new HashSet<>(test.values());

        return result;
    }


}


