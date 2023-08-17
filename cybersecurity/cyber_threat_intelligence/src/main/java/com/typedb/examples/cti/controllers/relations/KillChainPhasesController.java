package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.ext.KillChainPhases;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class KillChainPhasesController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public KillChainPhasesController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getKillChainPhases")
    @GetMapping(value = "/kill-chain-phases", produces = "application/json")
    public ObjectNode getKillChainPhasesJSON() {
        RelationDAO<KillChainPhases> killChainPhasesDAO = new RelationDAO<>(wrapper, KillChainPhases.nameRel,
                KillChainPhases.rolePlayers, KillChainPhases.typeString);
        return killChainPhasesDAO.findAll();
    }

}
