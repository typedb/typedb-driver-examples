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

package com.typedb.examples.fraud.db;

import com.typedb.examples.fraud.model.Cardholder;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CardholderDao implements Dao<Cardholder> {

  private static final String INSERT_QUERY_TEMPLATE =
      "match " +
      "  $bank isa Bank, has name \"%s\";" +
      "insert " +
      "  $cardholderCoords isa Geo_coordinate, has latitude %s, has longitude %s;" +
      "  $cardholderAddr isa Address, has street \"%s\", has city \"%s\", has state \"%s\", has zip %s;" +
      "  $cardholder isa Person, has first_name \"%s\", has last_name \"%s\", has gender \"%s\", has job \"%s\", has date_of_birth %s;" +
      "  $cardholderLoc (location: $cardholderAddr, geo: $cardholderCoords, identify: $cardholder) isa locate;" +
      "  $cardholderAccount (owner: $cardholder, attached_card: $cc, attached_bank: $bank) isa bank_account;" +
      "  $cc isa Card, has card_number %s;";

  protected static final String CARDHOLDER_MATCH =
      "  $cardholderCoords isa Geo_coordinate, has latitude $cardholderLat, has longitude $cardholderLon;" +
      "  $cardholderAddr isa Address, has street $street, has city $city, has state $state, has zip $zip;" +
      "  $cardholder isa Person, has first_name $firstName, has last_name $lastName, has gender $gender, has job $job, has date_of_birth $birthDate;" +
      "  $cardholderLoc (location: $cardholderAddr, geo: $cardholderCoords, identify: $cardholder) isa locate;" +
      "  $cc isa Card, has card_number $ccNum;" +
      "  $cardholderAccount (owner: $cardholder, attached_card: $cc, attached_bank: $bank) isa bank_account;";

  @Inject
  TypeDBSessionWrapper db;

  public Set<Cardholder> getAll() {

    var getQueryStr = "match " + CARDHOLDER_MATCH + BankDao.BANK_MATCH;

    var results = db.getAll(getQueryStr);

    var cardholders = results.stream().map(CardholderDao::fromResult).collect(Collectors.toSet());

    return cardholders;
  }

  public void insertAll(Set<Cardholder> cardholders) {

    var queries = cardholders.stream().map(this::getInsertQueryStr).collect(Collectors.toSet());

    db.insertAll(queries);
  }

  protected static Cardholder fromResult(Hashtable<String, String> result) {

    var cc = CreditCardDao.fromResult(result);
    var coords = CardholderCoordsDao.fromResult(result);
    var addr = AddressDao.fromResult(result);

    var firstName = result.get("firstName");
    var lastName = result.get("lastName");
    var gender = result.get("gender");
    var job = result.get("job");
    var birthDate = result.get("birthDate");

    var cardholder = new Cardholder(firstName, lastName, gender, job, birthDate, addr, coords, cc);

    return cardholder;
  }

  private String getInsertQueryStr(Cardholder cardholder) {

    var insertQueryStr = INSERT_QUERY_TEMPLATE.formatted(
        cardholder.getCc().getBank().getName(),
        cardholder.getCoords().getLatitude(),
        cardholder.getCoords().getLongitude(),
        cardholder.getAddress().getStreet(),
        cardholder.getAddress().getCity(),
        cardholder.getAddress().getState(),
        cardholder.getAddress().getZip(),
        cardholder.getFirstName(),
        cardholder.getLastName(),
        cardholder.getGender(),
        cardholder.getJob(),
        cardholder.getBirthDate(),
        cardholder.getCc().getNumber()
    );

    return insertQueryStr;
  }
}
