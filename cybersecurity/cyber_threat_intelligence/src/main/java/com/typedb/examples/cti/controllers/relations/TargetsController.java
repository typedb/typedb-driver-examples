package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.Targets;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TargetsController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public TargetsController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getTargets")
    @GetMapping(value = "/targets", produces = "application/json")
    public ObjectNode getTargetsJSON() {
        RelationDAO targetsDAO = new RelationDAO(wrapper, Targets.nameRel,
                Targets.rolePlayers, Targets.typeString);
        return targetsDAO.findAll();
    }

    @QueryMapping(value = "getTargetsSearch")
    @GetMapping(value = "/targets/{type}/{name}", produces = "application/json")
    public ObjectNode getTargetsSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO targetsDAO = new RelationDAO(wrapper, Targets.nameRel,
                Targets.rolePlayers, Targets.typeString);
        return targetsDAO.search(type, name);
    }

}
