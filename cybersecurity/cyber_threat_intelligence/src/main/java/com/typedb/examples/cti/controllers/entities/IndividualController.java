package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.identity.Individual;
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

public class IndividualController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public IndividualController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
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

}
