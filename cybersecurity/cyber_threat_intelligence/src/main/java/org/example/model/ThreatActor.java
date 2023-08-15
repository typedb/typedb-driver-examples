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

import java.util.Date;

public class ThreatActor extends StixDomainObject {
    @JsonIgnore
    public static final String typeString = StixDomainObject.typeString + " name; description; aliases; stix_role; goals; resource_level; primary_motivation; secondary_motivation; sophistication; personal_characteristics; roles; therat_actor_types;";

    private String name;
    private String description;
    private String aliases;
    private String stix_role;
    private Date first_seen;
    private Date last_seen;
    private String goals;
    private String resource_level;
    private String primary_motivation;
    private String secondary_motivation;
    private String sophistication;
    private String personal_characteristics;
    private String roles;
    private String threat_actor_types;

    public ThreatActor() {
    }

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

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getStix_role() {
        return stix_role;
    }

    public void setStix_role(String stix_role) {
        this.stix_role = stix_role;
    }

    public Date getFirst_seen() {
        return first_seen;
    }

    public void setFirst_seen(Date first_seen) {
        this.first_seen = first_seen;
    }

    public Date getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(Date last_seen) {
        this.last_seen = last_seen;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getResource_level() {
        return resource_level;
    }

    public void setResource_level(String resource_level) {
        this.resource_level = resource_level;
    }

    public String getPrimary_motivation() {
        return primary_motivation;
    }

    public void setPrimary_motivation(String primary_motivation) {
        this.primary_motivation = primary_motivation;
    }

    public String getSecondary_motivation() {
        return secondary_motivation;
    }

    public void setSecondary_motivation(String secondary_motivation) {
        this.secondary_motivation = secondary_motivation;
    }

    public String getSophistication() {
        return sophistication;
    }

    public void setSophistication(String sophistication) {
        this.sophistication = sophistication;
    }

    public String getPersonal_characteristics() {
        return personal_characteristics;
    }

    public void setPersonal_characteristics(String personal_characteristics) {
        this.personal_characteristics = personal_characteristics;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getThreat_actor_types() {
        return threat_actor_types;
    }

    public void setThreat_actor_types(String threat_actor_types) {
        this.threat_actor_types = threat_actor_types;
    }
}
