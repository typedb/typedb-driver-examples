package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.relationship.ext.ExternalReferences;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class ExternalReferencesController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public ExternalReferencesController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getExternalReferences")
    @GetMapping(value = "/external-references", produces = "application/json")
    public ObjectNode getExternalReferencesJSON() {
        RelationDAO externalReferencesDAO = new RelationDAO(wrapper, ExternalReferences.nameRel,
                ExternalReferences.rolePlayers, ExternalReferences.typeString);
        return externalReferencesDAO.findAll();
    }

}
