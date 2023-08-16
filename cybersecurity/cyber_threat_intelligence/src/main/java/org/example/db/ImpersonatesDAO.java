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
import org.example.model.domain.relationship.Impersonates;

import java.util.List;

public class ImpersonatesDAO {
    protected static final String IMPERSONATES_MATCH =
            "$ta (impersonating: $rp1, impersonated: $rp2) isa impersonates, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";
    private static final String NAME_REL = "impersonates";
    private final TypeDBSessionWrapper db;
    private final List<String> typeString;
    private final List<String> rolePlayers;


    public ImpersonatesDAO(TypeDBSessionWrapper db) {
        this.db = db;
        typeString = Impersonates.typeString;
        rolePlayers = Impersonates.rolePlayers;
    }

    private ObjectNode find(String getQueryStr) {
        return db.getRelJSON(getQueryStr, NAME_REL, rolePlayers);
    }

    public ObjectNode findAll() {
        var getQueryStr = "match " + IMPERSONATES_MATCH + "group $id; ";
        return find(getQueryStr);
    }

    public ObjectNode search(String attrType, String attrName) {

        if (typeString.contains(attrType)) {
            attrName = "\"" + attrName + "\"";
        }

        String search = "$ta has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + IMPERSONATES_MATCH + search + "group $id;";

        return find(getQueryStr);
    }

}
