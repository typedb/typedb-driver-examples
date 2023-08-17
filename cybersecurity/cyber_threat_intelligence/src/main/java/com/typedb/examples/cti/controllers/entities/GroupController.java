package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.identity.Group;
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

public class GroupController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public GroupController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
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

}
