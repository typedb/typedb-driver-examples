package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.Indicates;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class IndicatesController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public IndicatesController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getIndicates")
    @GetMapping(value = "/indicates", produces = "application/json")
    public ObjectNode getIndicatesJSON() {
        RelationDAO<Indicates> indicatesDAO = new RelationDAO<>(wrapper, Indicates.nameRel,
                Indicates.rolePlayers, Indicates.typeString);
        return indicatesDAO.findAll();
    }

    @QueryMapping(value = "getIndicatesSearch")
    @GetMapping(value = "/indicates/{type}/{name}", produces = "application/json")
    public ObjectNode getIndicatesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO<Indicates> indicatesDAO = new RelationDAO<>(wrapper, Indicates.nameRel,
                Indicates.rolePlayers, Indicates.typeString);
        return indicatesDAO.search(type, name);
    }

}
