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

package com.typedb.examples.cti.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.typedb.examples.cti.model.stix.StixCoreRelationship;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class Sighting extends StixCoreRelationship {
    @JsonIgnore
    public static final List<String> typeString = Stream.of(StixCoreRelationship.typeString, List.of("summary")
            ).flatMap(List::stream).toList();

    @JsonIgnore
    public static final List<String> rolePlayers = Arrays.asList("sighting_of", "observed_data");

    @JsonIgnore
    public static final String nameRel = "sighting";

    private Date firstSeen;
    private Date lastSeen;

    private Double count;
    private String summary;

    public Date getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Date firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
