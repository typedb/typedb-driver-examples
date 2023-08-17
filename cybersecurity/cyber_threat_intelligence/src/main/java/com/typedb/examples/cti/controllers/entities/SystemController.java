package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.object.identity.System;
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

public class SystemController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public SystemController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
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

}
