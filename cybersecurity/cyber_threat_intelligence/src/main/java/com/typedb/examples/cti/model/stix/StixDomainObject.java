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
import java.util.Set;
import java.util.stream.Stream;

public class StixDomainObject extends StixCoreObject {

    @JsonIgnore
    public static final List<String> typeList = Stream.of(StixCoreObject.typeList, List.of("labels")
        ).flatMap(List::stream).toList();

    @JsonIgnore
    public static final List<String> typeString = Stream.of(StixCoreObject.typeString, Arrays.asList("labels", "langs")
        ).flatMap(List::stream).toList();

    @JsonIgnore
    public static final String nameEnt = "stix_domain_object";


    private Date created;

    private Date modified;

    private Boolean revoked;

    private Set<String> labels;

    private int confidence;

    private String langs;

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

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getLangs() {
        return langs;
    }

    public void setLangs(String langs) {
        this.langs = langs;
    }
}
