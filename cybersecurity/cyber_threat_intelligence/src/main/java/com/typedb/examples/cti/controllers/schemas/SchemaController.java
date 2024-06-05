package com.typedb.examples.cti.controllers.schemas;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.SchemaDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SchemaController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public SchemaController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping
    @GetMapping(value = "/schema", produces = "application/json")
    public ObjectNode getSchema() {
        SchemaDAO schemaDAO = new SchemaDAO(wrapper);
        return schemaDAO.getSchemaAll();
    }

    @QueryMapping
    @GetMapping(value = "/schema-current", produces = "application/json")
    public ObjectNode getSchemaCurrent() {
        SchemaDAO schemaDAO = new SchemaDAO(wrapper);
        return schemaDAO.getSchemaCurrent();
    }

}
