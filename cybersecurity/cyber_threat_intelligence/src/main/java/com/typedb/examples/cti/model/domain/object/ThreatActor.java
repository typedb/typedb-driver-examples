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
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class ThreatActor extends StixDomainObject {
    @JsonIgnore
    public static final List<String> typeString = Stream.of(StixDomainObject.typeString,
            Arrays.asList("name", "description", "aliases", "stix_role", "goals", "resource_level",
            "primary_motivation", "secondary_motivation", "sophistication", "personal_characteristics",
            "roles", "threat_actor_types")).flatMap(List::stream).toList();

    @JsonIgnore
    public static final String nameEnt = "threat_actor";

    private String name;
    private String description;
    private String aliases;
    private String stixRole;
    private Date firstSeen;
    private Date lastSeen;
    private String goals;
    private String resourceLevel;
    private String primaryMotivation;
    private String secondaryMotivation;
    private String sophistication;
    private String personalCharacteristics;
    private String roles;
    private String threatActorTypes;

    public ThreatActor() {
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

    public String getStixRole() {
        return stixRole;
    }

    public void setStixRole(String stixRole) {
        this.stixRole = stixRole;
    }

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

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getResourceLevel() {
        return resourceLevel;
    }

    public void setResourceLevel(String resourceLevel) {
        this.resourceLevel = resourceLevel;
    }

    public String getPrimaryMotivation() {
        return primaryMotivation;
    }

    public void setPrimaryMotivation(String primaryMotivation) {
        this.primaryMotivation = primaryMotivation;
    }

    public String getSecondaryMotivation() {
        return secondaryMotivation;
    }

    public void setSecondaryMotivation(String secondaryMotivation) {
        this.secondaryMotivation = secondaryMotivation;
    }

    public String getSophistication() {
        return sophistication;
    }

    public void setSophistication(String sophistication) {
        this.sophistication = sophistication;
    }

    public String getPersonalCharacteristics() {
        return personalCharacteristics;
    }

    public void setPersonalCharacteristics(String personalCharacteristics) {
        this.personalCharacteristics = personalCharacteristics;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getThreatActorTypes() {
        return threatActorTypes;
    }

    public void setThreatActorTypes(String threatActorTypes) {
        this.threatActorTypes = threatActorTypes;
    }
}
