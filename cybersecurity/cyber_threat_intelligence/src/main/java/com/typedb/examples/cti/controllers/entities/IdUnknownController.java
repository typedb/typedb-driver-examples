package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.identity.IdUnknown;
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

public class IdUnknownController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public IdUnknownController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
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

}
