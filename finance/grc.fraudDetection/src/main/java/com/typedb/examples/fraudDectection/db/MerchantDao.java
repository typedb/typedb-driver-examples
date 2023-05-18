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

package com.typedb.examples.fraudDectection.db;

import com.typedb.examples.fraudDectection.model.Merchant;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class MerchantDao implements Dao<Merchant> {

  private static final String INSERT_QUERY_TEMPLATE =
      "insert " +
      "  $merchant isa Company, has name \"%s\", has company_type \"%s\";" +
      "  $merchantCoords isa Geo_coordinate, has latitude %s, has longitude %s;" +
      "  $merchantGeo (coordinates: $merchantCoords, transacting_party: $merchant) isa geolocate;";

  protected static final String MERCHANT_MATCH =
      "  $merchant isa Company, has name $merchantName, has company_type $merchantType;" +
      "  $merchantCoords isa Geo_coordinate, has latitude $merchantLat, has longitude $merchantLon;" +
      "  $merchantGeo (coordinates: $merchantCoords, transacting_party: $merchant) isa geolocate;";

  protected static final String MERCHANT_MATCH_NAME =
      "  $merchantName = \"%s\";";

  @Inject
  TypeDBSessionWrapper db;

  public Set<Merchant> getAll() {

    var getQueryStr = "match " + MERCHANT_MATCH;

    return getMerchants(getQueryStr);
  }

  public Set<Merchant> getByName(String name){

    var matchName = MERCHANT_MATCH_NAME.formatted(name);
    var getQueryStr = "match " + MERCHANT_MATCH + matchName;

    return getMerchants(getQueryStr);
  }

  private Set<Merchant> getMerchants(String query){

    var results = db.getAll(query);
    var merchants = results.stream().map(MerchantDao::fromResult).collect(Collectors.toSet());

    return merchants;
  }

  public void insertAll(Set<Merchant> merchants) {

    var queries = merchants.stream().map(this::getInsertQueryStr).collect(Collectors.toSet());

    db.insertAll(queries);
  }

  protected static Merchant fromResult(Hashtable<String, String> result) {

    var coords = MerchantCoordsDao.fromResult(result);

    var merchant = new Merchant(result.get("merchantName"), result.get("merchantType"), coords);

    return merchant;
  }

  private String getInsertQueryStr(Merchant merchant) {

    var insertQueryStr = INSERT_QUERY_TEMPLATE.formatted(
        merchant.getName(),
        merchant.getCategory(),
        merchant.getCoords().getLatitude(),
        merchant.getCoords().getLongitude()
    );

    return insertQueryStr;
  }
}
