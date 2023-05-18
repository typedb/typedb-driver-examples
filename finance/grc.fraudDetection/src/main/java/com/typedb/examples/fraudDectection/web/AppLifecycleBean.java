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

package com.typedb.examples.fraudDectection.web;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.typedb.examples.fraudDectection.db.Dao;
import com.typedb.examples.fraudDectection.db.TransactionDao;
import com.typedb.examples.fraudDectection.model.Bank;
import com.typedb.examples.fraudDectection.model.BankCoordinates;
import com.typedb.examples.fraudDectection.model.Cardholder;
import com.typedb.examples.fraudDectection.model.Merchant;
import com.typedb.examples.fraudDectection.model.Transaction;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import io.quarkus.runtime.StartupEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

  private static final Logger LOGGER = Logger.getLogger("AppLifecycleBean");

  @Inject
  TypeDBClient client;

  @Inject
  Dao<Bank> banks;
  @Inject
  Dao<Cardholder> cardholders;
  @Inject
  Dao<Merchant> merchants;
  @Inject
  TransactionDao transactions;

  void onStart(@Observes StartupEvent ev) {

    LOGGER.info("Deleting database");

    client.databases().get("fraud").delete();

    LOGGER.info("Creating database");

    client.databases().create("fraud");

    LOGGER.info("Creating schema");

    try (TypeDBSession session = client.session("fraud", TypeDBSession.Type.SCHEMA)) {

      try (TypeDBTransaction tx = session.transaction(TypeDBTransaction.Type.WRITE)) {

        URL file = this.getClass().getClassLoader().getResource("schema.tql");

        assert file != null;

        String query = Files.readString(Paths.get(file.toURI()));

        tx.query().define(TypeQL.parseQuery(query).asDefine());

        tx.commit();
      }
      catch (Exception ex) {

        ex.printStackTrace(System.err);
      }
    }

    LOGGER.info("Preparing data");

    var sampleBanks = new HashSet<Bank>();
    var sampleCardholders = new HashSet<Cardholder>();
    var sampleMerchants = new HashSet<Merchant>();
    var sampleTransactions = new HashSet<Transaction>();

    sampleBanks.add(new Bank("ABC", new BankCoordinates("30.5", "-90.3")));
    sampleBanks.add(new Bank("MNO", new BankCoordinates("33.986391", "-81.200714")));
    sampleBanks.add(new Bank("QRS", new BankCoordinates("43.7", "-88.2")));
    sampleBanks.add(new Bank("XYZ", new BankCoordinates("40.98", "-90.4")));

    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("data.csv")) {

      assert is != null;

      try (InputStreamReader isr = new InputStreamReader(is)) {

        CsvToBean<Transaction> csv = new CsvToBeanBuilder<Transaction>(isr).withType(Transaction.class).build();

        sampleTransactions.addAll(csv.parse());

        sampleTransactions.forEach(tx -> {

          Bank bank = sampleBanks.stream().skip((int) (sampleBanks.size() * Math.random())).findFirst().get();

          tx.getCardholder().getCc().setBank(bank);
        });

        sampleMerchants.addAll(sampleTransactions.stream().map(Transaction::getMerchant).collect(Collectors.toSet()));
        sampleCardholders.addAll(sampleTransactions.stream().map(Transaction::getCardholder).collect(Collectors.toSet()));
      }
    }
    catch (Exception ex) {
      LOGGER.error(ex);
    }

    LOGGER.info("Inserting data");

    banks.insertAll(sampleBanks);
    cardholders.insertAll(sampleCardholders);
    merchants.insertAll(sampleMerchants);
    transactions.insertAll(sampleTransactions);
  }
}
