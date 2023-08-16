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

package com.typedb.examples.cti.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.*;
import com.typedb.examples.cti.model.domain.object.Identity;
import com.typedb.examples.cti.model.domain.object.ThreatActor;
import com.typedb.examples.cti.model.domain.object.identity.*;
import com.typedb.examples.cti.model.domain.observable.File;
import com.typedb.examples.cti.model.domain.relationship.*;
import com.typedb.examples.cti.model.domain.relationship.ext.*;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.typedb.examples.cti.model.domain.object.Indicator;
import com.typedb.examples.cti.model.domain.object.Malware;
import com.typedb.examples.cti.model.domain.object.identity.Class;
import com.typedb.examples.cti.model.domain.object.identity.System;
import com.typedb.examples.cti.model.domain.stix.ext.KillChainPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class Controller {
    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public Controller(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getMalware")
    @GetMapping(value = "/malware", produces = "application/json")
    public ObjectNode getMalwareJSON() {
        EntityDAO<Malware> malwareDAO = new EntityDAO<>(wrapper, Malware.nameEnt, Malware.typeString);
        return malwareDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/malware-beans", produces = "application/json")
    public Set<Malware> getMalwareBeans() throws JsonProcessingException {
        EntityDAO<Malware> malwareDAO = new EntityDAO<>(wrapper, Malware.nameEnt, Malware.typeString);
        return malwareDAO.findAllBeans();
    }

    @QueryMapping(value = "getThreatActor")
    @GetMapping(value = "/threat-actor", produces = "application/json")
    public ObjectNode getThreatActorJSON() {
        EntityDAO<ThreatActor> threatActorDAO = new EntityDAO<>(wrapper, ThreatActor.nameEnt, ThreatActor.typeString);
        return threatActorDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/threat-actor-beans", produces = "application/json")
    public Set<ThreatActor> getThreatActorBeans() throws JsonProcessingException {
        EntityDAO<ThreatActor> threatActorDAO = new EntityDAO<>(wrapper, ThreatActor.nameEnt, ThreatActor.typeString);
        return threatActorDAO.findAllBeans();
    }

    @QueryMapping(value = "getFile")
    @GetMapping(value = "/file", produces = "application/json")
    public ObjectNode getFileJSON() {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/file-beans", produces = "application/json")
    public Set<File> getFileBeans() throws JsonProcessingException {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.findAllBeans();
    }


    @QueryMapping(value = "getIdentity")
    @GetMapping(value = "/identity", produces = "application/json")
    public ObjectNode getIdentityJSON() {
        EntityDAO<Identity> identityDAO = new EntityDAO<>(wrapper, Identity.nameEnt, Identity.typeString);
        return identityDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/identity-beans", produces = "application/json")
    public Set<Identity> getIdentityBeans() throws JsonProcessingException {
        EntityDAO<Identity> identityDAO = new EntityDAO<>(wrapper, Identity.nameEnt, Identity.typeString);
        return identityDAO.findAllBeans();
    }

    @QueryMapping(value = "getIndicator")
    @GetMapping(value = "/indicator", produces = "application/json")
    public ObjectNode getIndicatorJSON() {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.findAll();
    }


    @QueryMapping
    @GetMapping(value = "/indicator-beans", produces = "application/json")
    public Set<Indicator> getIndicatorBeans() throws JsonProcessingException {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.findAllBeans();
    }

    @QueryMapping(value = "getClass")
    @GetMapping(value = "/class", produces = "application/json")
    public ObjectNode getClassJSON() {
        EntityDAO<Class> classDAO = new EntityDAO<>(wrapper, Class.nameEnt, Class.typeString);
        return classDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/class-beans", produces = "application/json")
    public Set<Class> getClassBeans() throws JsonProcessingException {
        EntityDAO<Class> classDAO = new EntityDAO<>(wrapper, Class.nameEnt, Class.typeString);
        return classDAO.findAllBeans();
    }

    @QueryMapping(value = "getGroup")
    @GetMapping(value = "/group", produces = "application/json")
    public ObjectNode getGroupJSON() {
        EntityDAO<Group> groupDAO = new EntityDAO<>(wrapper, Group.nameEnt, Group.typeString);
        return groupDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/group-beans", produces = "application/json")
    public Set<Group> getGroupBeans() throws JsonProcessingException {
        EntityDAO<Group> groupDAO = new EntityDAO<>(wrapper, Group.nameEnt, Group.typeString);
        return groupDAO.findAllBeans();
    }

    @QueryMapping(value = "getIdUnknown")
    @GetMapping(value = "/id-unknown", produces = "application/json")
    public ObjectNode getIdUnknownJSON() {
        EntityDAO<IdUnknown> idUnknownDAO = new EntityDAO<>(wrapper, IdUnknown.nameEnt, IdUnknown.typeString);
        return idUnknownDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/id-unknown-beans", produces = "application/json")
    public Set<IdUnknown> getIdUnknownBeans() throws JsonProcessingException {
        EntityDAO<IdUnknown> idUnknownDAO = new EntityDAO<>(wrapper, IdUnknown.nameEnt, IdUnknown.typeString);
        return idUnknownDAO.findAllBeans();
    }

    @QueryMapping(value = "getIndividual")
    @GetMapping(value = "/individual", produces = "application/json")
    public ObjectNode getIndividualJSON() {
        EntityDAO<Individual> individualDAO = new EntityDAO<>(wrapper, Individual.nameEnt, Individual.typeString);
        return individualDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/individual-beans", produces = "application/json")
    public Set<Individual> getIndividualBeans() throws JsonProcessingException {
        EntityDAO<Individual> individualDAO = new EntityDAO<>(wrapper, Individual.nameEnt, Individual.typeString);
        return individualDAO.findAllBeans();
    }

    @QueryMapping(value = "getSystem")
    @GetMapping(value = "/system", produces = "application/json")
    public ObjectNode getSystemJSON() {
        EntityDAO<System> systemDAO = new EntityDAO<>(wrapper, System.nameEnt, System.typeString);
        return systemDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/system-beans", produces = "application/json")
    public Set<System> getSystemBeans() throws JsonProcessingException {
        EntityDAO<System> systemDAO = new EntityDAO<>(wrapper, System.nameEnt, System.typeString);
        return systemDAO.findAllBeans();
    }

    @QueryMapping(value = "getKillChainPhase")
    @GetMapping(value = "/kill-chain-phase", produces = "application/json")
    public ObjectNode getKillChainPhaseJSON() {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/kill-chain-phase-beans", produces = "application/json")
    public Set<KillChainPhase> getKillChainPhaseBeans() throws JsonProcessingException {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.findAllBeans();
    }

    @QueryMapping(value = "getAttributedTo")
    @GetMapping(value = "/attributed-to", produces = "application/json")
    public ObjectNode getAttributedToJSON() {
        RelationDAO<AttributedTo> attributedToDAO = new RelationDAO<>(wrapper, AttributedTo.nameRel,
                AttributedTo.rolePlayers, AttributedTo.typeString);
        return attributedToDAO.findAll();
    }

    @QueryMapping(value = "getIndicates")
    @GetMapping(value = "/indicates", produces = "application/json")
    public ObjectNode getIndicatesJSON() {
        RelationDAO<Indicates> indicatesDAO = new RelationDAO<>(wrapper, Indicates.nameRel,
                Indicates.rolePlayers, Indicates.typeString);
        return indicatesDAO.findAll();
    }

    @QueryMapping(value = "getSighting")
    @GetMapping(value = "/sighting", produces = "application/json")
    public ObjectNode getSightingJSON() {
        RelationDAO<Sighting> sightingDAO = new RelationDAO<>(wrapper, Sighting.nameRel,
                Sighting.rolePlayers, Sighting.typeString);
        return sightingDAO.findAll();
    }

    @QueryMapping(value = "getUses")
    @GetMapping(value = "/uses", produces = "application/json")
    public ObjectNode getUsesJSON() {
        RelationDAO<Uses> usesDAO = new RelationDAO<>(wrapper, Uses.nameRel,
                Uses.rolePlayers, Uses.typeString);
        return usesDAO.findAll();
    }

    @QueryMapping(value = "getTargets")
    @GetMapping(value = "/targets", produces = "application/json")
    public ObjectNode getTargetsJSON() {
        RelationDAO<Targets> targetsDAO = new RelationDAO<>(wrapper, Targets.nameRel,
                Targets.rolePlayers, Targets.typeString);
        return targetsDAO.findAll();
    }

    @QueryMapping(value = "getImpersonates")
    @GetMapping(value = "/impersonates", produces = "application/json")
    public ObjectNode getImpersonatesJSON() {
        RelationDAO<Impersonates> impersonatesDAO = new RelationDAO<>(wrapper, Impersonates.nameRel,
                Impersonates.rolePlayers, Impersonates.typeString);
        return impersonatesDAO.findAll();
    }

    @QueryMapping(value = "getKillChainPhases")
    @GetMapping(value = "/kill-chain-phases", produces = "application/json")
    public ObjectNode getKillChainPhasesJSON() {
        RelationDAO<KillChainPhases> killChainPhasesDAO = new RelationDAO<>(wrapper, KillChainPhases.nameRel,
                KillChainPhases.rolePlayers, KillChainPhases.typeString);
        return killChainPhasesDAO.findAll();
    }

    @QueryMapping(value = "getExternalReferences")
    @GetMapping(value = "/external-references", produces = "application/json")
    public ObjectNode getExternalReferencesJSON() {
        RelationDAO<ExternalReferences> externalReferencesDAO = new RelationDAO<>(wrapper, ExternalReferences.nameRel,
                ExternalReferences.rolePlayers, ExternalReferences.typeString);
        return externalReferencesDAO.findAll();
    }

    @QueryMapping(value = "getCreatedBy")
    @GetMapping(value = "/created-by", produces = "application/json")
    public ObjectNode getCreatedByJSON() {
        RelationDAO<CreatedBy> createdByDAO = new RelationDAO<>(wrapper, CreatedBy.nameRel,
                CreatedBy.rolePlayers, CreatedBy.typeString);

        return createdByDAO.findAll();
    }

    @QueryMapping(value = "getHashes")
    @GetMapping(value = "/hashes", produces = "application/json")
    public ObjectNode getHashesJSON() {
        RelationDAO<Hashes> hashesDAO = new RelationDAO<>(wrapper, Hashes.nameRel,
                Hashes.rolePlayers, Hashes.typeString);
        return hashesDAO.findAll();
    }

    @QueryMapping(value = "getThreatActorSearch")
    @GetMapping(value = "/threat-actor/{type}/{name}", produces = "application/json")
    public ObjectNode getThreatActorSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<ThreatActor> threatActorDAO = new EntityDAO<>(wrapper, ThreatActor.nameEnt, ThreatActor.typeString);
        return threatActorDAO.search(type, name);
    }


    @QueryMapping(value = "getThreatActorSearchBeans")
    @GetMapping("/threat-actor-beans/{type}/{name}")
    public Set<ThreatActor> getThreatActorSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<ThreatActor> threatActorDAO = new EntityDAO<>(wrapper, ThreatActor.nameEnt, ThreatActor.typeString);
        return threatActorDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getMalwareSearch")
    @GetMapping(value = "/malware/{type}/{name}", produces = "application/json")
    public ObjectNode getMalwareSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<Malware> malwareDAO = new EntityDAO<>(wrapper, Malware.nameEnt, Malware.typeString);
        return malwareDAO.search(type, name);
    }

    @QueryMapping(value = "getMalwareSearchBeans")
    @GetMapping("/malware-beans/{type}/{name}")
    public Set<Malware> getMalwareSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<Malware> malwareDAO = new EntityDAO<>(wrapper, Malware.nameEnt, Malware.typeString);
        return malwareDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getFileSearch")
    @GetMapping(value = "/file/{type}/{name}", produces = "application/json")
    public ObjectNode getFileSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.search(type, name);
    }

    @QueryMapping(value = "getFileSearchBeans")
    @GetMapping("/file-beans/{type}/{name}")
    public Set<File> getFileSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.searchBeans(type, name);
    }


    @QueryMapping(value = "getIdentitySearch")
    @GetMapping(value = "/identity/{type}/{name}", produces = "application/json")
    public ObjectNode getIdentitySearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<Identity> identityDAO = new EntityDAO<>(wrapper, Identity.nameEnt, Identity.typeString);
        return identityDAO.search(type, name);
    }

    @QueryMapping(value = "getIdentitySearchBeans")
    @GetMapping("/identity-beans/{type}/{name}")
    public Set<Identity> getIdentitySearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<Identity> identityDAO = new EntityDAO<>(wrapper, Identity.nameEnt, Identity.typeString);
        return identityDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getIndicatorSearch")
    @GetMapping(value = "/indicator/{type}/{name}", produces = "application/json")
    public ObjectNode getIndicatorSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.search(type, name);
    }

    @QueryMapping(value = "getIndicatorSearchBeans")
    @GetMapping("/indicator-beans/{type}/{name}")
    public Set<Indicator> getIndicatorSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getClassSearch")
    @GetMapping(value = "/class/{type}/{name}", produces = "application/json")
    public ObjectNode getClassSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<Class> classDAO = new EntityDAO<>(wrapper, Class.nameEnt, Class.typeString);
        return classDAO.search(type, name);
    }

    @QueryMapping(value = "getClassSearchBeans")
    @GetMapping("/class-beans/{type}/{name}")
    public Set<Class> getClassSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<Class> classDAO = new EntityDAO<>(wrapper, Class.nameEnt, Class.typeString);
        return classDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getGroupSearch")
    @GetMapping(value = "/group/{type}/{name}", produces = "application/json")
    public ObjectNode getGroupSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<Group> groupDAO = new EntityDAO<>(wrapper, Group.nameEnt, Group.typeString);
        return groupDAO.search(type, name);
    }

    @QueryMapping(value = "getGroupSearchBeans")
    @GetMapping("/group-beans/{type}/{name}")
    public Set<Group> getGroupSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<Group> groupDAO = new EntityDAO<>(wrapper, Group.nameEnt, Group.typeString);
        return groupDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getIdUnknownSearch")
    @GetMapping(value = "/id-unknown/{type}/{name}", produces = "application/json")
    public ObjectNode getIdUnknownSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<IdUnknown> idUnknownDAO = new EntityDAO<>(wrapper, IdUnknown.nameEnt, IdUnknown.typeString);
        return idUnknownDAO.search(type, name);
    }

    @QueryMapping(value = "getIdUnknownSearchBeans")
    @GetMapping("/id-unknown-beans/{type}/{name}")
    public Set<IdUnknown> getIdUnknownSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<IdUnknown>idUnknownDAO = new EntityDAO<>(wrapper, IdUnknown.nameEnt, IdUnknown.typeString);
        return idUnknownDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getIndividualSearch")
    @GetMapping(value = "/individual/{type}/{name}", produces = "application/json")
    public ObjectNode getIndividualSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<Individual> individualDAO = new EntityDAO<>(wrapper, Individual.nameEnt, Individual.typeString);
        return individualDAO.search(type, name);
    }

    @QueryMapping(value = "getIndividualSearchBeans")
    @GetMapping("/individual-beans/{type}/{name}")
    public Set<Individual> getIndividualSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<Individual> individualDAO = new EntityDAO<>(wrapper, Individual.nameEnt, Individual.typeString);
        return individualDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getSystemSearch")
    @GetMapping(value = "/system/{type}/{name}", produces = "application/json")
    public ObjectNode getSystemSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<System> systemDAO = new EntityDAO<>(wrapper, System.nameEnt, System.typeString);
        return systemDAO.search(type, name);
    }

    @QueryMapping(value = "getSystemSearchBeans")
    @GetMapping("/system-beans/{type}/{name}")
    public Set<System> getSystemSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<System> systemDAO = new EntityDAO<>(wrapper, System.nameEnt, System.typeString);
        return systemDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getKillChainPhaseSearch")
    @GetMapping(value = "/kill-chain-phase/{type}/{name}", produces = "application/json")
    public ObjectNode getKillChainPhaseSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.search(type, name);
    }

    @QueryMapping(value = "getKillChainPhaseSearchBeans")
    @GetMapping("/kill-chain-phase-beans/{type}/{name}")
    public Set<KillChainPhase> getKillChainPhaseSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getAttributedToSearch")
    @GetMapping(value = "/attributed-to/{type}/{name}", produces = "application/json")
    public ObjectNode getAttributedToSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<AttributedTo> attributedToDAO = new RelationDAO<>(wrapper, AttributedTo.nameRel,
                AttributedTo.rolePlayers, AttributedTo.typeString);
        return attributedToDAO.search(type, name);
    }

    @QueryMapping(value = "getIndicatesSearch")
    @GetMapping(value = "/indicates/{type}/{name}", produces = "application/json")
    public ObjectNode getIndicatesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<Indicates> indicatesDAO = new RelationDAO<>(wrapper, Indicates.nameRel,
                Indicates.rolePlayers, Indicates.typeString);
        return indicatesDAO.search(type, name);
    }

    @QueryMapping(value = "getSightingSearch")
    @GetMapping(value = "/sighting/{type}/{name}", produces = "application/json")
    public ObjectNode getSightingSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<Sighting> sightingDAO = new RelationDAO<>(wrapper, Sighting.nameRel,
                Sighting.rolePlayers, Sighting.typeString);
        return sightingDAO.search(type, name);
    }

    @QueryMapping(value = "getUsesSearch")
    @GetMapping(value = "/uses/{type}/{name}", produces = "application/json")
    public ObjectNode getUsesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<Uses> usesDAO = new RelationDAO<>(wrapper, Uses.nameRel,
                Uses.rolePlayers, Uses.typeString);
        return usesDAO.search(type, name);
    }

    @QueryMapping(value = "getTargetsSearch")
    @GetMapping(value = "/targets/{type}/{name}", produces = "application/json")
    public ObjectNode getTargetsSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<Targets> targetsDAO = new RelationDAO<>(wrapper, Targets.nameRel,
                Targets.rolePlayers, Targets.typeString);
        return targetsDAO.search(type, name);
    }

    @QueryMapping(value = "getImpersonatesSearch")
    @GetMapping(value = "/impersonates/{type}/{name}", produces = "application/json")
    public ObjectNode getImpersonatesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<Impersonates> impersonatesDAO = new RelationDAO<>(wrapper, Impersonates.nameRel,
                Impersonates.rolePlayers, Impersonates.typeString);
        return impersonatesDAO.search(type, name);
    }

    @QueryMapping
    @GetMapping(value = "/schema", produces = "application/json")
    public ObjectNode getSchema() {
        SchemaDAO schemaDAO = new SchemaDAO(wrapper);
        return schemaDAO.getSchemaAll();
    }

    @QueryMapping
    @GetMapping(value = "/schema-current", produces = "application/json")
    public ObjectNode getSchemaCurrent() {
        SchemaDAO schemaDAO = new SchemaDAO(wrapper);
        return schemaDAO.getSchemaCurrent();
    }

}
