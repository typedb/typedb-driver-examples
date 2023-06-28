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
import org.example.model.Impersonates;

public class ImpersonatesDAO {
    private final TypeDBSessionWrapper db;
    private final Impersonates impersonates;

    private final String nameRel = "impersonates";
    private final String typeString;

    protected static final String IMPERSONATES_MATCH =
            "$ta (impersonating: $AAA, impersonated: $BBB) isa impersonates, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";

    public ImpersonatesDAO(TypeDBSessionWrapper db) {
        this.db = db;
        impersonates = new Impersonates();
        typeString = impersonates.getTypeString();
    }

    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + IMPERSONATES_MATCH + "group $id; ";
        return db.getRelJSON(getQueryStr, nameRel ,impersonates.getRolePlayers());
    }

    public ObjectNode getSearchJSON(String type, String name) {

        if (typeString.contains(" " + type + ";")){
            name = "\"" + name + "\"";
        }

        String search = "$ta has " + type + " = " + name + ";";
        var getQueryStr = "match " + IMPERSONATES_MATCH + search + "group $id;";

        return db.getRelJSON(getQueryStr, nameRel, impersonates.getRolePlayers());
    }

}
