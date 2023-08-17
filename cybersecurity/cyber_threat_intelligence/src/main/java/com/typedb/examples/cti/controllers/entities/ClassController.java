package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.object.identity.Class;
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

public class ClassController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public ClassController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
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
}
