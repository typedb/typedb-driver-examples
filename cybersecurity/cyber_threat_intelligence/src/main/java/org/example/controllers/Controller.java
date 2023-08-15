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

package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.example.configuration.AppConfiguration;
import org.example.db.*;
import org.example.model.*;
import org.example.model.Class;
import org.example.model.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.web.bind.annotation.*;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.Set;
import java.util.logging.Logger;

@RestController
public class Controller {
    private static final Logger LOGGER = Logger.getLogger("Controller");
    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public Controller(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getMalware")
    @GetMapping(value = "/malware", produces = "application/json")
    public ObjectNode getMalwareJSON(){
        MalwareDAO malwareDAO = new MalwareDAO(wrapper);
        return malwareDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/malware-beans", produces = "application/json")
    public Set<Malware> getMalwareBeans() throws JsonProcessingException {
        MalwareDAO malwareDAO = new MalwareDAO(wrapper);
        return malwareDAO.getAllBeans();
    }

    @QueryMapping(value = "getThreatActor")
    @GetMapping(value = "/threat-actor", produces = "application/json")
    public ObjectNode getThreatActorJSON(){
        ThreatActorDAO threatActorDAO = new ThreatActorDAO(wrapper);
        return threatActorDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/threat-actor-beans", produces = "application/json")
    public Set<ThreatActor> getThreatActorBeans() throws JsonProcessingException {
        ThreatActorDAO threatActorDAO = new ThreatActorDAO(wrapper);
        return threatActorDAO.getAllBeans();
    }

    @QueryMapping(value = "getFile")
    @GetMapping(value = "/file", produces = "application/json")
    public ObjectNode getFileJSON(){
        FileDAO fileDAO = new FileDAO(wrapper);
        return fileDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/file-beans", produces = "application/json")
    public Set<File> getFileBeans() throws JsonProcessingException {
        FileDAO fileDAO = new FileDAO(wrapper);
        return fileDAO.getAllBeans();
    }


    @QueryMapping(value = "getIdentity")
    @GetMapping(value = "/identity", produces = "application/json")
    public ObjectNode getIdentityJSON(){
        IdentityDAO identityDAO = new IdentityDAO(wrapper);
        return identityDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/identity-beans", produces = "application/json")
    public Set<Identity> getIdentityBeans() throws JsonProcessingException {
        IdentityDAO identityDAO = new IdentityDAO(wrapper);
        return identityDAO.getAllBeans();
    }

    @QueryMapping(value = "getIndicator")
    @GetMapping(value = "/indicator", produces = "application/json")
    public ObjectNode getIndicatorJSON(){
        IndicatorDAO indicatorDAO = new IndicatorDAO(wrapper);
        return indicatorDAO.getAllJSON();
    }


    @QueryMapping
    @GetMapping(value = "/indicator-beans", produces = "application/json")
    public Set<Indicator> getIndicatorBeans() throws JsonProcessingException {
        IndicatorDAO indicatorDAO = new IndicatorDAO(wrapper);
        return indicatorDAO.getAllBeans();
    }

    @QueryMapping(value = "getClass")
    @GetMapping(value = "/class", produces = "application/json")
    public ObjectNode get_ClassJSON(){
        ClassDAO classDAO = new ClassDAO(wrapper);
        return classDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/class-beans", produces = "application/json")
    public Set<Class> get_ClassBeans() throws JsonProcessingException {
        ClassDAO classDAO = new ClassDAO(wrapper);
        return classDAO.getAllBeans();
    }

    @QueryMapping(value = "getGroup")
    @GetMapping(value = "/group", produces = "application/json")
    public ObjectNode getGroupJSON(){
        GroupDAO groupDAO = new GroupDAO(wrapper);
        return groupDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/group-beans", produces = "application/json")
    public Set<Group> getGroupBeans() throws JsonProcessingException {
        GroupDAO groupDAO = new GroupDAO(wrapper);
        return groupDAO.getAllBeans();
    }

    @QueryMapping(value = "getIdUnknown")
    @GetMapping(value = "/id-unknown", produces = "application/json")
    public ObjectNode getIdUnknownJSON(){
        IdUnknownDAO idUnknownDAO = new IdUnknownDAO(wrapper);
        return idUnknownDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/id-unknown-beans", produces = "application/json")
    public Set<IdUnknown> getIdUnknownBeans() throws JsonProcessingException {
        IdUnknownDAO idUnknownDAO = new IdUnknownDAO(wrapper);
        return idUnknownDAO.getAllBeans();
    }

    @QueryMapping(value = "getIndividual")
    @GetMapping(value = "/individual", produces = "application/json")
    public ObjectNode getIndividualJSON(){
        IndividualDAO individualDAO = new IndividualDAO(wrapper);
        return individualDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/individual-beans", produces = "application/json")
    public Set<Individual> getIndividualBeans() throws JsonProcessingException {
        IndividualDAO individualDAO = new IndividualDAO(wrapper);
        return individualDAO.getAllBeans();
    }

    @QueryMapping(value = "getSystem")
    @GetMapping(value = "/system", produces = "application/json")
    public ObjectNode getSystemJSON(){
        SystemDAO systemDAO = new SystemDAO(wrapper);
        return systemDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/system-beans", produces = "application/json")
    public Set<System> getSystemBeans() throws JsonProcessingException {
        SystemDAO systemDAO = new SystemDAO(wrapper);
        return systemDAO.getAllBeans();
    }

    @QueryMapping(value = "getKillChainPhase")
    @GetMapping(value = "/kill-chain-phase", produces = "application/json")
    public ObjectNode getKillChainPhaseJSON(){
        KillChainPhaseDAO killChainPhaseDAO = new KillChainPhaseDAO(wrapper);
        return killChainPhaseDAO.getAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/kill-chain-phase-beans", produces = "application/json")
    public Set<KillChainPhase> getKillChainPhaseBeans() throws JsonProcessingException {
        KillChainPhaseDAO killChainPhaseDAO = new KillChainPhaseDAO(wrapper);
        return killChainPhaseDAO.getAllBeans();
    }

    @QueryMapping(value = "getAttributedTo")
    @GetMapping(value = "/attributed-to", produces = "application/json")
    public ObjectNode getAttributedToJSON(){
        AttributedToDAO attributedToDAO = new AttributedToDAO(wrapper);
        return attributedToDAO.getAllJSON();
    }

    @QueryMapping(value = "getIndicates")
    @GetMapping(value = "/indicates", produces = "application/json")
    public ObjectNode getIndicatesJSON(){
        IndicatesDAO indicatesDAO = new IndicatesDAO(wrapper);
        return indicatesDAO.getAllJSON();
    }

    @QueryMapping(value = "getSighting")
    @GetMapping(value = "/sighting", produces = "application/json")
    public ObjectNode getSightingJSON(){
        SightingDAO sightingDAO = new SightingDAO(wrapper);
        return sightingDAO.getAllJSON();
    }

    @QueryMapping(value = "getUses")
    @GetMapping(value = "/uses", produces = "application/json")
    public ObjectNode getUsesJSON(){
        UsesDAO usesDAO = new UsesDAO(wrapper);
        return usesDAO.getAllJSON();
    }

    @QueryMapping(value = "getTargets")
    @GetMapping(value = "/targets", produces = "application/json")
    public ObjectNode getTargetsJSON(){
        TargetsDAO targetsDAO = new TargetsDAO(wrapper);
        return targetsDAO.getAllJSON();
    }

    @QueryMapping(value = "getImpersonates")
    @GetMapping(value = "/impersonates", produces = "application/json")
    public ObjectNode getImpersonatesJSON(){
        ImpersonatesDAO impersonatesDAO = new ImpersonatesDAO(wrapper);
        return impersonatesDAO.getAllJSON();
    }

    @QueryMapping(value = "getKillChainPhases")
    @GetMapping(value = "/kill-chain-phases", produces = "application/json")
    public ObjectNode getKillChainPhasesJSON(){
        KillChainPhasesDAO killChainPhasesDAO = new KillChainPhasesDAO(wrapper);
        return killChainPhasesDAO.getAllJSON();
    }

    @QueryMapping(value = "getExternalReferences")
    @GetMapping(value = "/external-references", produces = "application/json")
    public ObjectNode getExternalReferencesJSON(){
        ExternalReferencesDAO externalReferencesDAO = new ExternalReferencesDAO(wrapper);
        return externalReferencesDAO.getAllJSON();
    }

    @QueryMapping(value = "getCreatedBy")
    @GetMapping(value = "/created-by", produces = "application/json")
    public ObjectNode getCreatedByJSON(){
        CreatedByDAO createdByDAO = new CreatedByDAO(wrapper);
        return createdByDAO.getAllJSON();
    }

    @QueryMapping(value = "getHashes")
    @GetMapping(value = "/hashes", produces = "application/json")
    public ObjectNode getHashesJSON(){
        HashesDAO hashesDAO = new HashesDAO(wrapper);
        return hashesDAO.getAllJSON();
    }

    @QueryMapping(value = "getThreatActorSearch")
    @GetMapping(value = "/threat-actor/{type}/{name}", produces = "application/json")
    public ObjectNode getThreatActorSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        ThreatActorDAO threatActorDAO = new ThreatActorDAO(wrapper);
        return threatActorDAO.getSearchJSON(type, name);
    }


    @QueryMapping(value = "getThreatActorSearchBeans")
    @GetMapping("/threat-actor-beans/{type}/{name}")
    public Set<ThreatActor> getThreatActorSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        ThreatActorDAO threatActorDAO = new ThreatActorDAO(wrapper);
        return threatActorDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getMalwareSearch")
    @GetMapping(value = "/malware/{type}/{name}", produces = "application/json")
    public ObjectNode getMalwareSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        MalwareDAO malwareDAO = new MalwareDAO(wrapper);
        return malwareDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getMalwareSearchBeans")
    @GetMapping("/malware-beans/{type}/{name}")
    public Set<Malware> getMalwareSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        MalwareDAO malwareDAO = new MalwareDAO(wrapper);
        return malwareDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getFileSearch")
    @GetMapping(value = "/file/{type}/{name}", produces = "application/json")
    public ObjectNode getFileSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        FileDAO fileDAO = new FileDAO(wrapper);
        return fileDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getFileSearchBeans")
    @GetMapping("/file-beans/{type}/{name}")
    public Set<File> getFileSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        FileDAO fileDAO = new FileDAO(wrapper);
        return fileDAO.getSearchBeans(type, name);
    }


    @QueryMapping(value = "getIdentitySearch")
    @GetMapping(value = "/identity/{type}/{name}", produces = "application/json")
    public ObjectNode getIdentitySearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        IdentityDAO identityDAO = new IdentityDAO(wrapper);
        return identityDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getIdentitySearchBeans")
    @GetMapping("/identity-beans/{type}/{name}")
    public Set<Identity> getIdentitySearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        IdentityDAO identityDAO = new IdentityDAO(wrapper);
        return identityDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getIndicatorSearch")
    @GetMapping(value = "/indicator/{type}/{name}", produces = "application/json")
    public ObjectNode getIndicatorSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        IndicatorDAO indicatorDAO = new IndicatorDAO(wrapper);
        return indicatorDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getIndicatorSearchBeans")
    @GetMapping("/indicator-beans/{type}/{name}")
    public Set<Indicator> getIndicatorSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        IndicatorDAO indicatorDAO = new IndicatorDAO(wrapper);
        return indicatorDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getClassSearch")
    @GetMapping(value = "/class/{type}/{name}", produces = "application/json")
    public ObjectNode getClassSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        ClassDAO classDAO = new ClassDAO(wrapper);
        return classDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getClassSearchBeans")
    @GetMapping("/class-beans/{type}/{name}")
    public Set<Class> getClassSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        ClassDAO classDAO = new ClassDAO(wrapper);
        return classDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getGroupSearch")
    @GetMapping(value = "/group/{type}/{name}", produces = "application/json")
    public ObjectNode getGroupSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        GroupDAO groupDAO = new GroupDAO(wrapper);
        return groupDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getGroupSearchBeans")
    @GetMapping("/group-beans/{type}/{name}")
    public Set<Group> getGroupSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        GroupDAO groupDAO = new GroupDAO(wrapper);
        return groupDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getIdUnknownSearch")
    @GetMapping(value = "/id-unknown/{type}/{name}", produces = "application/json")
    public ObjectNode getIdUnknownSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        IdUnknownDAO idUnknownDAO = new IdUnknownDAO(wrapper);
        return idUnknownDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getIdUnknownSearchBeans")
    @GetMapping("/id-unknown-beans/{type}/{name}")
    public Set<IdUnknown> getIdUnknownSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        IdUnknownDAO idUnknownDAO = new IdUnknownDAO(wrapper);
        return idUnknownDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getIndividualSearch")
    @GetMapping(value = "/individual/{type}/{name}", produces = "application/json")
    public ObjectNode getIndividualSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        IndividualDAO individualDAO = new IndividualDAO(wrapper);
        return individualDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getIndividualSearchBeans")
    @GetMapping("/individual-beans/{type}/{name}")
    public Set<Individual> getIndividualSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        IndividualDAO individualDAO = new IndividualDAO(wrapper);
        return individualDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getSystemSearch")
    @GetMapping(value = "/system/{type}/{name}", produces = "application/json")
    public ObjectNode getSystemSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        SystemDAO systemDAO = new SystemDAO(wrapper);
        return systemDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getSystemSearchBeans")
    @GetMapping("/system-beans/{type}/{name}")
    public Set<System> getSystemSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        SystemDAO systemDAO = new SystemDAO(wrapper);
        return systemDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getKillChainPhaseSearch")
    @GetMapping(value = "/kill-chain-phase/{type}/{name}", produces = "application/json")
    public ObjectNode getKillChainPhaseSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        KillChainPhaseDAO killChainPhaseDAO = new KillChainPhaseDAO(wrapper);
        return killChainPhaseDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getKillChainPhaseSearchBeans")
    @GetMapping("/kill-chain-phase-beans/{type}/{name}")
    public Set<KillChainPhase> getKillChainPhaseSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        KillChainPhaseDAO killChainPhaseDAO = new KillChainPhaseDAO(wrapper);
        return killChainPhaseDAO.getSearchBeans(type, name);
    }

    @QueryMapping(value = "getAttributedToSearch")
    @GetMapping(value = "/attributed-to/{type}/{name}", produces = "application/json")
    public ObjectNode getAttributedToSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        AttributedToDAO attributedToDAO = new AttributedToDAO(wrapper);
        return attributedToDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getIndicatesSearch")
    @GetMapping(value = "/indicates/{type}/{name}", produces = "application/json")
    public ObjectNode getIndicatesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        IndicatesDAO indicatesDAO = new IndicatesDAO(wrapper);
        return indicatesDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getSightingSearch")
    @GetMapping(value = "/sighting/{type}/{name}", produces = "application/json")
    public ObjectNode getSightingSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        SightingDAO sightingDAO = new SightingDAO(wrapper);
        return sightingDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getUsesSearch")
    @GetMapping(value = "/uses/{type}/{name}", produces = "application/json")
    public ObjectNode getUsesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        UsesDAO usesDAO = new UsesDAO(wrapper);
        return usesDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getTargetsSearch")
    @GetMapping(value = "/targets/{type}/{name}", produces = "application/json")
    public ObjectNode getTargetsSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        TargetsDAO targetsDAO = new TargetsDAO(wrapper);
        return targetsDAO.getSearchJSON(type, name);
    }

    @QueryMapping(value = "getImpersonatesSearch")
    @GetMapping(value = "/impersonates/{type}/{name}", produces = "application/json")
    public ObjectNode getImpersonatesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name){
        ImpersonatesDAO impersonatesDAO = new ImpersonatesDAO(wrapper);
        return impersonatesDAO.getSearchJSON(type, name);
    }

    @QueryMapping
    @GetMapping(value = "/schema", produces = "application/json")
    public ObjectNode getSchema(){
        SchemaDAO schemaDAO = new SchemaDAO(wrapper);
        return schemaDAO.getSchemaAllJSON();
    }

    @QueryMapping
    @GetMapping(value = "/schema-current", produces = "application/json")
    public ObjectNode getSchemaCurrent(){
        SchemaDAO schemaDAO = new SchemaDAO(wrapper);
        return schemaDAO.getSchemaCurrentJSON();
    }
    
}
