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
import org.example.model.Indicates;

public class IndicatesDAO {
    private final TypeDBSessionWrapper db;
    private final Indicates indicates;

    private final String nameRel = "indicates";
    private final String typeString;

    protected static final String INDICATES_MATCH =
            "$ta (indicating: $AAA, indicated: $BBB) isa indicates, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";

    public IndicatesDAO(TypeDBSessionWrapper db) {
        this.db = db;
        indicates = new Indicates();
        typeString = indicates.getTypeString();
    }

    private ObjectNode getJSON(String getQueryStr) {
        return db.getRelJSON(getQueryStr, nameRel ,indicates.getRolePlayers());
    }

    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + INDICATES_MATCH + "group $id; ";
        return getJSON(getQueryStr);
    }

    public ObjectNode getSearchJSON(String type, String name) {

        if (typeString.contains(" " + type + ";")){
            name = "\"" + name + "\"";
        }

        String search = "$ta has " + type + " = " + name + ";";
        var getQueryStr = "match " + INDICATES_MATCH + search + "group $id;";

        return getJSON(getQueryStr);
    }

}
