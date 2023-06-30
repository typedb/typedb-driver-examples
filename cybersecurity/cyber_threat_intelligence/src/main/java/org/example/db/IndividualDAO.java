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
import org.example.model.Individual;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IndividualDAO {
    TypeDBSessionWrapper db;
    String typeString;

    protected static final String INDIVIDUAL_MATCH =
            "  $individual isa individual, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";

    public IndividualDAO(TypeDBSessionWrapper db) {
        this.db = db;
        Individual tempIndividual = new Individual();
        typeString = tempIndividual.getTypeString();
    }

    private ObjectNode getJSON(String getQueryStr) {
        return db.getAllJSON(getQueryStr);
    }

    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + INDIVIDUAL_MATCH + "group $id; ";
        return getJSON(getQueryStr);
    }

    public String getAllString() {
        return getAllJSON().toString();
    }

    public Set<Individual> getAllBeans() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String getQueryStr = "match " + INDIVIDUAL_MATCH + "group $id;";
        ObjectNode json = getJSON(getQueryStr);
        Map<String, Individual> test = objectMapper.readValue(json.toString(), new TypeReference<Map<String, Individual>>(){});
        Set<Individual> result = new HashSet<>(test.values());

        return result;
    }

    public ObjectNode getSearchJSON(String type, String name) {

        if (typeString.contains(" " + type + ";")){
            name = "\"" + name + "\"";
        }

        String search = "$individual has " + type + " = " + name + ";";
        var getQueryStr = "match " + INDIVIDUAL_MATCH + search + "group $id;";

        return getJSON(getQueryStr);
    }

    public String getSearchString(String type, String name) {
        return getSearchJSON(type, name).toString();
    }

    public Set<Individual> getSearchBeans(String type, String name) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (typeString.contains(" " + type + ";")){
            name = "\"" + name + "\"";
        }

        String search = "$individual has " + type + " = " + name + ";";

        String getQueryStr = "match " + INDIVIDUAL_MATCH + search + " group $id;";
        ObjectNode json = getJSON(getQueryStr);
        Map<String, Individual> test = objectMapper.readValue(json.toString(), new TypeReference<Map<String, Individual>>(){});
        Set<Individual> result = new HashSet<>(test.values());

        return result;
    }


}


