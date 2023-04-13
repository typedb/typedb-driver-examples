package com.typedb.examples.fraud.db;

import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBOptions;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@RequestScoped
public class TypeDBSessionWrapper {

  private static final Logger LOGGER = Logger.getLogger(TypeDBSessionWrapper.class);

  @Inject
  TypeDBClient client;

  @ConfigProperty(name = "typedb.db", defaultValue="fraud")
  String db;

  private TypeDBSession session;

  public void insertAll(Set<String> queries) {

    try (TypeDBTransaction writeTx = session.transaction(TypeDBTransaction.Type.WRITE)) {

      queries.forEach(query -> writeTx.query().insert(query));

      writeTx.commit();
    }
  }

  public Set<Hashtable<String, String>> getAll(String query) {

    try (TypeDBTransaction readTx = session.transaction(TypeDBTransaction.Type.READ)) {

      var dbResults = readTx.query().match(query);

      var results = dbResults.map(conceptMap -> {

        var result = new Hashtable<String, String>();

        conceptMap.map().entrySet().stream().filter(e -> e.getValue().isAttribute()).forEach(e -> {

            var attrName = e.getKey();
            var attrValue = e.getValue().asAttribute().getValue().toString();

            LOGGER.debug("Adding attribute to result: name = " + attrName + ", value = " + attrValue);

            result.put(attrName, attrValue);
        });

        LOGGER.debug("Adding result");

        return result;
      });

      return results.collect(Collectors.toSet());
    }
  }

  @PostConstruct
  void initialize() {

    LOGGER.info("Creating TypeDB session");

    session = client.session(db, TypeDBSession.Type.DATA, TypeDBOptions.core().infer(true));
  }

  @PreDestroy
  void destroy() {

    LOGGER.info("Closing TypeDB session");

    session.close();
  }
}
