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
import org.example.model.KillChainPhases;

public class KillChainPhasesDAO {
    private final TypeDBSessionWrapper db;
    private final KillChainPhases kill_chain_phases;

    private final String nameRel = "kill_chain_phases";
    private final String typeString;

    protected static final String KILL_CHAIN_PHASES_MATCH =
            "$ta (using: $AAA, used: $BBB) isa kill_chain_phases;";

    public KillChainPhasesDAO(TypeDBSessionWrapper db) {
        this.db = db;
        kill_chain_phases = new KillChainPhases();
        typeString = kill_chain_phases.getTypeString();
    }

    private ObjectNode getJSON(String getQueryStr) {
        return db.getListJSON(getQueryStr, nameRel ,kill_chain_phases.getRolePlayers());
    }

    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + KILL_CHAIN_PHASES_MATCH + "group $ta; ";
        return getJSON(getQueryStr);
    }

    public ObjectNode getSearchJSON(String attrType, String attrName) {

        if (typeString.contains(" " + attrType + ";")){
            attrName = "\"" + attrName + "\"";
        }

        String search = "$ta has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + KILL_CHAIN_PHASES_MATCH + search + "group $ta;";

        return getJSON(getQueryStr);
    }

}
