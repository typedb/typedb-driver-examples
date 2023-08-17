package com.typedb.examples.cti.controllers.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.configuration.AppConfiguration;
import com.typedb.examples.cti.db.EntityDAO;
import com.typedb.examples.cti.db.TypeDBSessionWrapper;
import com.typedb.examples.cti.model.observable.File;
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

public class FileController {

    private final TypeDBSessionWrapper wrapper;

    @Autowired
    public FileController(AppConfiguration appConfiguration) {
        TypeDBClient client = TypeDB.coreClient(appConfiguration.getAddress() + ":" + appConfiguration.getPort());
        wrapper = new TypeDBSessionWrapper(client, appConfiguration);
    }

    @QueryMapping(value = "getFile")
    @GetMapping(value = "/file", produces = "application/json")
    public ObjectNode getFileJSON() {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.findAll();
    }

    @QueryMapping
    @GetMapping(value = "/file-beans", produces = "application/json")
    public Set<File> getFileBeans() throws JsonProcessingException {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.findAllBeans();
    }

    @QueryMapping(value = "getFileSearch")
    @GetMapping(value = "/file/{type}/{name}", produces = "application/json")
    public ObjectNode getFileSearchJSON(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.search(type, name);
    }

    @QueryMapping(value = "getFileSearchBeans")
    @GetMapping("/file-beans/{type}/{name}")
    public Set<File> getFileSearchBeans(@Argument("type") @PathVariable String type, @Argument("name") @PathVariable String name) throws JsonProcessingException {
        EntityDAO<File> fileDAO = new EntityDAO<>(wrapper, File.nameEnt, File.typeString);
        return fileDAO.searchBeans(type, name);
    }
}
