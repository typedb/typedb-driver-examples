package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.ext.CreatedBy;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class CreatedByController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public CreatedByController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getCreatedBy")
    @GetMapping(value = "/created-by", produces = "application/json")
    public ObjectNode getCreatedByJSON() {
        RelationDAO<CreatedBy> createdByDAO = new RelationDAO<>(wrapper, CreatedBy.nameRel,
                CreatedBy.rolePlayers, CreatedBy.typeString);

        return createdByDAO.findAll();
    }

}
