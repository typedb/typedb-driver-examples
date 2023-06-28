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

package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StixCyberObservableObject extends StixCoreObject {
    private String defanged;

    @JsonIgnore
    private String typeList = super.getTypeList();
    @JsonIgnore
    private String typeString = super.getTypeString() + " defanged;";

    @Override
    public String getTypeList() {
        return typeList;
    }

    @Override
    public String getTypeString() {
        return typeString;
    }

    public StixCyberObservableObject() {
    }

    public String getDefanged() {
        return defanged;
    }

    public void setDefanged(String defanged) {
        this.defanged = defanged;
    }
}
