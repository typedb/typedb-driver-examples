package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.Uses;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class UsesController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public UsesController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getUses")
    @GetMapping(value = "/uses", produces = "application/json")
    public ObjectNode getUsesJSON() {
        RelationDAO<Uses> usesDAO = new RelationDAO<>(wrapper, Uses.nameRel,
                Uses.rolePlayers, Uses.typeString, true);
        return usesDAO.findAll();
    }

    @QueryMapping(value = "getUsesSearch")
    @GetMapping(value = "/uses/{type}/{name}", produces = "application/json")
    public ObjectNode getUsesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<Uses> usesDAO = new RelationDAO<>(wrapper, Uses.nameRel,
                Uses.rolePlayers, Uses.typeString);
        return usesDAO.search(type, name);
    }

}
