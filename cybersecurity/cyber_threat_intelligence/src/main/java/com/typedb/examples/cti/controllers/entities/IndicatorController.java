package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.object.Indicator;
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

public class IndicatorController{

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public IndicatorController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getIndicator")
    @GetMapping(value = "/indicator", produces = "application/json")
    public ObjectNode getIndicatorJSON() {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/indicator-beans", produces = "application/json")
    public Set<Indicator> getIndicatorBeans() throws JsonProcessingException {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.findAllBeans();
    }

    @QueryMapping(value = "getIndicatorSearch")
    @GetMapping(value = "/indicator/{type}/{name}", produces = "application/json")
    public ObjectNode getIndicatorSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.search(type, name);
    }

    @QueryMapping(value = "getIndicatorSearchBeans")
    @GetMapping("/indicator-beans/{type}/{name}")
    public Set<Indicator> getIndicatorSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<Indicator> indicatorDAO = new EntityDAO<>(wrapper, Indicator.nameEnt, Indicator.typeString);
        return indicatorDAO.searchBeans(type, name);
    }
}
