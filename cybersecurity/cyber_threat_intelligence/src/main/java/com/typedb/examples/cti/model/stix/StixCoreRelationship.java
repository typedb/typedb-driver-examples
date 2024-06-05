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

package com.typedb.examples.cti.model.stix;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StixCoreRelationship {
    @JsonIgnore
    public static final List<String> typeString = Arrays.asList("stix_id", "stix_type");

    @JsonIgnore
    public static final String nameEnt = "stix_core_relationship";
    private String stixId;
    private String stixType;
    private Date created;
    private Date modified;

    public String getStixId() {
        return stixId;
    }

    public void setStixId(String stixId) {
        this.stixId = stixId;
    }

    public String getStixType() {
        return stixType;
    }

    public void setStixType(String stixType) {
        this.stixType = stixType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
