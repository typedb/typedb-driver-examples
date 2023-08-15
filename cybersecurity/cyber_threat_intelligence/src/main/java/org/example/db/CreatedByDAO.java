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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.model.CreatedBy;

public class CreatedByDAO {
    protected static final String CREATED_BY_MATCH =
            "$ta (creator: $AAA, created: $BBB) isa created_by;";
    private static final String NAME_REL = "created_by";
    private final TypeDBSessionWrapper db;
    private final String typeString;
    private final String rolePlayers;


    public CreatedByDAO(TypeDBSessionWrapper db) {
        this.db = db;
        typeString = CreatedBy.typeString;
        rolePlayers = CreatedBy.rolePlayers;
    }

    private ObjectNode find(String getQueryStr) {
        return db.getListJSON(getQueryStr, NAME_REL, rolePlayers);
    }

    public ObjectNode findAll() {
        var getQueryStr = "match " + CREATED_BY_MATCH + "group $ta; ";
        return find(getQueryStr);
    }

    public ObjectNode search(String attrType, String attrName) {

        if (typeString.contains(" " + attrType + ";")) {
            attrName = "\"" + attrName + "\"";
        }

        String search = "$ta has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + CREATED_BY_MATCH + search + "group $ta;";

        return find(getQueryStr);
    }

}
