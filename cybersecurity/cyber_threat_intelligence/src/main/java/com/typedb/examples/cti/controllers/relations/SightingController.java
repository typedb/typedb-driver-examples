package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.Sighting;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SightingController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public SightingController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getSighting")
    @GetMapping(value = "/sighting", produces = "application/json")
    public ObjectNode getSightingJSON() {
        RelationDAO sightingDAO = new RelationDAO(wrapper, Sighting.nameRel,
                Sighting.rolePlayers, Sighting.typeString);
        return sightingDAO.findAll();
    }

    @QueryMapping(value = "getSightingSearch")
    @GetMapping(value = "/sighting/{type}/{name}", produces = "application/json")
    public ObjectNode getSightingSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO sightingDAO = new RelationDAO(wrapper, Sighting.nameRel,
                Sighting.rolePlayers, Sighting.typeString);
        return sightingDAO.search(type, name);
    }

}
