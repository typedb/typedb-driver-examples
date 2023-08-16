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

package com.typedb.examples.cti.model.domain.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.typedb.examples.cti.model.domain.stix.StixDomainObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Identity extends StixDomainObject {
    @JsonIgnore
    public static final List<String> typeString = Stream.of(StixDomainObject.typeString,
            Arrays.asList("name", "description", "stix_role", "identity_class", "sector",
                    "contact_information")).flatMap(List::stream).toList();

    @JsonIgnore
    public static final String nameEnt = "identity";
    private String name;
    private String description;
    private String stixRole;
    private String identityClass;
    private String sector;
    private String contactInformation;

    public Identity() {
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

    public String getStixRole() {
        return stixRole;
    }

    public void setStixRole(String stixRole) {
        this.stixRole = stixRole;
    }

    public String getIdentityClass() {
        return identityClass;
    }

    public void setIdentityClass(String identityClass) {
        this.identityClass = identityClass;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
}
