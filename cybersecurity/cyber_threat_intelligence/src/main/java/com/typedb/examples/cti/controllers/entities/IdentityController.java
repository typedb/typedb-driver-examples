package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.Identity;
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

public class IdentityController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public IdentityController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
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

}