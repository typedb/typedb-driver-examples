/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.typedb.examples.fraudDetection.db;

import com.vaticle.typedb.driver.api.TypeDBDriver;
import com.vaticle.typedb.driver.api.TypeDBOptions;
import com.vaticle.typedb.driver.api.TypeDBSession;
import com.vaticle.typedb.driver.api.TypeDBTransaction;
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
  TypeDBDriver driver;

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

      var dbResults = readTx.query().get(query);

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

    session = driver.session(db, TypeDBSession.Type.DATA, TypeDBOptions().infer(true));
  }

  @PreDestroy
  void destroy() {

    LOGGER.info("Closing TypeDB session");

    session.close();
  }
}
