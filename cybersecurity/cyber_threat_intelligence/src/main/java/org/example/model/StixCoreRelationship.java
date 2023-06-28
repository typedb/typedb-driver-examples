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

import java.util.Date;

public class StixCoreRelationship {
    private String spec_version;
    private String stix_id;
    private String stix_type;
    private Date created;
    private Date modified;

    private final String typeString = " stix_id; stix_version; stix_type;";

    public String getTypeString() {
        return typeString;
    }

    public String getSpec_version() {
        return spec_version;
    }

    public void setSpec_version(String spec_version) {
        this.spec_version = spec_version;
    }

    public String getStix_id() {
        return stix_id;
    }

    public void setStix_id(String stix_id) {
        this.stix_id = stix_id;
    }

    public String getStix_type() {
        return stix_type;
    }

    public void setStix_type(String stix_type) {
        this.stix_type = stix_type;
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
