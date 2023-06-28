package org.example.web;

import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import org.example.Configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Component
public class CTIBean implements ApplicationListener<ApplicationReadyEvent> {
    private TypeDBClient client;
    private static final Logger LOGGER = Logger.getLogger("AppSpringWeb");

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String address = appConfiguration.getAddress() + ":" + appConfiguration.getPort();
        String database = appConfiguration.getDatabase();
        String dataset = appConfiguration.getDataset();
        String schema = appConfiguration.getSchema();
        client = TypeDB.coreClient(address);
        LOGGER.info("Deleting Database");
        if(client.databases().contains(database)) {
            client.databases().get(database).delete();
        }
        LOGGER.info("Creating Database");
        client.databases().create(database);
        LOGGER.info("Inserting Schema");

        try (TypeDBSession session = client.session(database, TypeDBSession.Type.SCHEMA)) {
            try (TypeDBTransaction tx = session.transaction(TypeDBTransaction.Type.WRITE)) {
                URL file = this.getClass().getClassLoader().getResource(schema);

                if (file == null) {
                    throw new AssertionError();
                }

                String query = Files.readString(Paths.get(file.toURI()));
                tx.query().define(TypeQL.parseQuery(query).asDefine());
                tx.commit();
            } catch (Exception ex) {

                ex.printStackTrace(System.err);
            }

        }

        LOGGER.info("Inserting data");

        try (TypeDBSession session = client.session(database, TypeDBSession.Type.DATA)) {
            try (TypeDBTransaction tx = session.transaction(TypeDBTransaction.Type.WRITE)) {
                URL file = this.getClass().getClassLoader().getResource(dataset);

                if (file == null) {
                    throw new AssertionError();
                }

                String query = Files.readString(Paths.get(file.toURI()));
                tx.query().insert(TypeQL.parseQuery(query).asInsert());
                tx.commit();
            } catch (Exception ex) {

                ex.printStackTrace(System.err);
            }
        }

        LOGGER.info("Ready to use");
    }

}