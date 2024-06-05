package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.relationship.Impersonates;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class ImpersonatesController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public ImpersonatesController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getImpersonates")
    @GetMapping(value = "/impersonates", produces = "application/json")
    public ObjectNode getImpersonatesJSON() {
        RelationDAO impersonatesDAO = new RelationDAO(wrapper, Impersonates.nameRel,
                Impersonates.rolePlayers, Impersonates.typeString);
        return impersonatesDAO.findAll();
    }

    @QueryMapping(value = "getImpersonatesSearch")
    @GetMapping(value = "/impersonates/{type}/{name}", produces = "application/json")
    public ObjectNode getImpersonatesSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO impersonatesDAO = new RelationDAO(wrapper, Impersonates.nameRel,
                Impersonates.rolePlayers, Impersonates.typeString);
        return impersonatesDAO.search(type, name);
    }

}
