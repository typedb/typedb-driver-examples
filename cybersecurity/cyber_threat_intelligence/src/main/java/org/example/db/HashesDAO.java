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
import org.example.model.Hashes;

public class HashesDAO {

    private final TypeDBSessionWrapper db;
    private final Hashes hashes;

    private final String nameRel = "hashes";
    private final String typeString;

    protected static final String HASHES_MATCH =
            "$ta (hash_value: $AAA, hashes_owner: $BBB) isa hashes, has $attribute;";

    public HashesDAO(TypeDBSessionWrapper db) {
        this.db = db;
        hashes = new Hashes();
        typeString = hashes.getTypeString();
    }

    private ObjectNode getJSON(String getQueryStr) {
        return db.getListAttrJSON(getQueryStr, nameRel ,hashes.getRolePlayers(), true);
    }

    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + HASHES_MATCH + "group $ta; ";
        return getJSON(getQueryStr);
    }


}
