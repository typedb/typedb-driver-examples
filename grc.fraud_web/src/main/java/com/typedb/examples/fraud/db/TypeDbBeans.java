package com.typedb.examples.fraud.db;

import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

public class TypeDbBeans {

  private static final Logger LOGGER = Logger.getLogger(TypeDbBeans.class);

  @ConfigProperty(name = "typedb.host", defaultValue="localhost")
  String host;
  @ConfigProperty(name = "typedb.port", defaultValue="1729")
  String port;

  @Produces
  @ApplicationScoped
  TypeDBClient getClient() {

    LOGGER.info("Creating TypeDB client");

    return TypeDB.coreClient(host + ":" + port);
  }

   void closeClient(@Disposes TypeDBClient client) {

     LOGGER.info("Closing TypeDB client");

     client.close();
   }
}
