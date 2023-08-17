/*
 * Copyright (C) 2023 Vaticle
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

package com.typedb.examples.cti.web;

import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import com.typedb.examples.cti.configuration.AppConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Component
public class CTIBean implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = Logger.getLogger("AppSpringWeb");

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        String address = appConfiguration.getAddress() + ":" + appConfiguration.getPort();
        String database = appConfiguration.getDatabase();
        String dataset = appConfiguration.getDataset();
        String schema = appConfiguration.getSchema();
        TypeDBClient client = TypeDB.coreClient(address);
        LOGGER.info("Deleting Database");
        if (client.databases().contains(database)) {
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