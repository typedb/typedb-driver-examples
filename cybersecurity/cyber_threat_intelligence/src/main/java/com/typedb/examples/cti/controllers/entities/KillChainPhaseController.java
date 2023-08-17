package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.stix.ext.KillChainPhase;
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

public class KillChainPhaseController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public KillChainPhaseController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getKillChainPhaseSearch")
    @GetMapping(value = "/kill-chain-phase/{type}/{name}", produces = "application/json")
    public ObjectNode getKillChainPhaseSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.search(type, name);
    }

    @QueryMapping(value = "getKillChainPhaseSearchBeans")
    @GetMapping("/kill-chain-phase-beans/{type}/{name}")
    public Set<KillChainPhase> getKillChainPhaseSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.searchBeans(type, name);
    }

    @QueryMapping(value = "getKillChainPhase")
    @GetMapping(value = "/kill-chain-phase", produces = "application/json")
    public ObjectNode getKillChainPhaseJSON() {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/kill-chain-phase-beans", produces = "application/json")
    public Set<KillChainPhase> getKillChainPhaseBeans() throws JsonProcessingException {
        EntityDAO<KillChainPhase> killChainPhaseDAO = new EntityDAO<>(wrapper, KillChainPhase.nameEnt, KillChainPhase.typeString);
        return killChainPhaseDAO.findAllBeans();
    }

}
