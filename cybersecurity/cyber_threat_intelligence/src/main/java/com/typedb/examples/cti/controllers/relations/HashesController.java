package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.ext.Hashes;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HashesController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public HashesController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getHashes")
    @GetMapping(value = "/hashes", produces = "application/json")
    public ObjectNode getHashesJSON() {
        RelationDAO hashesDAO = new RelationDAO(wrapper, Hashes.nameRel,
                Hashes.rolePlayers, Hashes.typeString, true);
        return hashesDAO.findAll();
    }

    @QueryMapping(value = "getHashesSearch")
    @GetMapping(value = "/hashes/{type}/{name}", produces = "application/json")
    public ObjectNode getHashesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO hashesDAO = new RelationDAO(wrapper, Hashes.nameRel,
                Hashes.rolePlayers, Hashes.typeString, true);
        return hashesDAO.search(type, name);
    }

}
