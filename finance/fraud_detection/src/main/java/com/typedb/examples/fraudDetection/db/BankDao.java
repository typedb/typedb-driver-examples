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

import com.typedb.examples.fraudDetection.model.Bank;
import com.typedb.examples.fraudDetection.model.BankCoordinates;

import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class BankDao implements Dao<Bank> {

  private static final String INSERT_QUERY_TEMPLATE =
      "insert " +
      "  $bank isa Bank, has name \"%s\", has company_type \"Bank\";" +
      "  $bankCoords isa Geo_coordinate, has latitude %s, has longitude %s;" +
      "  $bankGeo (coordinates: $bankCoords, transacting_party: $bank) isa geolocate;";

  protected static final String BANK_MATCH =
      "  $bank isa Bank, has name $bankName;" +
      "  $bankCoords isa Geo_coordinate, has latitude $bankLat, has longitude $bankLon;" +
      "  $bankGeo (coordinates: $bankCoords, transacting_party: $bank) isa geolocate;";

  protected static final String BANK_MATCH_NAME =
      "  $bankName = \"%s\";";
  @Inject
  TypeDBSessionWrapper db;

  public Set<Bank> getAll() {

    var getQueryStr = "match " + BANK_MATCH + "; get;";

    return getBanks(getQueryStr);
  }

  public Set<Bank> getByName(String name){

    var matchName = BANK_MATCH_NAME.formatted(name);
    var getQueryStr = "match " + BANK_MATCH + matchName + "; get;";

    return getBanks(getQueryStr);
  }

  public void insertAll(Set<Bank> banks) {

    var queries = banks.stream().map(this::getInsertQueryStr).collect(Collectors.toSet());

    db.insertAll(queries);
  }

  private Set<Bank> getBanks(String query){

    var results = db.getAll(query);
    var banks = results.stream().map(BankDao::fromResult).collect(Collectors.toSet());

    return banks;
  }

  protected static Bank fromResult(Hashtable<String, String> result) {

    var lat = result.get("bankLat");
    var lon = result.get("bankLon");

    var coords = new BankCoordinates(lat, lon);

    var bank = new Bank(result.get("bankName"), coords);

    return bank;
  }

  private String getInsertQueryStr(Bank bank) {

    var insertQueryStr = INSERT_QUERY_TEMPLATE.formatted(
        bank.getName(),
        bank.getCoords().getLatitude(),
        bank.getCoords().getLongitude()
    );

    return insertQueryStr;
  }
}
