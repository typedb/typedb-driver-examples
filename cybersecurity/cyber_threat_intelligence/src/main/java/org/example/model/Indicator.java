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
import lombok.Builder;
import lombok.Data;

import java.util.Date;

public class Indicator extends StixDomainObject {

    private String name;
    private String description;
    private String pattern;
    private String pattern_type;
    private String pattern_version;
    private Date valid_from;
    private Date valid_until;

    @JsonIgnore
    private final String typeString = super.getTypeString() + " name; description; pattern; pattern_type; pattern_version;";


    public Indicator() {
    }

    @Override
    public String getTypeString() {
        return typeString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern_type() {
        return pattern_type;
    }

    public void setPattern_type(String pattern_type) {
        this.pattern_type = pattern_type;
    }

    public String getPattern_version() {
        return pattern_version;
    }

    public void setPattern_version(String pattern_version) {
        this.pattern_version = pattern_version;
    }

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getValid_until() {
        return valid_until;
    }

    public void setValid_until(Date valid_until) {
        this.valid_until = valid_until;
    }
}
