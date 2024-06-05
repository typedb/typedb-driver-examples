package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.ThreatActor;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController

public class ThreatActorController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public ThreatActorController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
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


}
