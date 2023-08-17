package com.typedb.examples.cti.controllers.relations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.RelationDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.domain.relationship.ext.AttributedTo;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class AttributedToController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public AttributedToController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getAttributedTo")
    @GetMapping(value = "/attributed-to", produces = "application/json")
    public ObjectNode getAttributedToJSON() {
        RelationDAO attributedToDAO = new RelationDAO(wrapper, AttributedTo.nameRel,
                AttributedTo.rolePlayers, AttributedTo.typeString);
        return attributedToDAO.findAll();
    }

    @QueryMapping(value = "getAttributedToSearch")
    @GetMapping(value = "/attributed-to/{type}/{name}", produces = "application/json")
    public ObjectNode getAttributedToSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        RelationDAO attributedToDAO = new RelationDAO(wrapper, AttributedTo.nameRel,
                AttributedTo.rolePlayers, AttributedTo.typeString);
        return attributedToDAO.search(type, name);
    }

}
