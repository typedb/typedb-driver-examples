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

package com.typedb.examples.fraudDetection.web;

import com.typedb.examples.fraudDetection.db.Dao;
import com.typedb.examples.fraudDetection.db.TransactionDao;
import com.typedb.examples.fraudDetection.model.Bank;
import com.typedb.examples.fraudDetection.model.Cardholder;
import com.typedb.examples.fraudDetection.model.Merchant;
import com.typedb.examples.fraudDetection.model.Transaction;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class GqlResource {

  @Inject
  Dao<Bank> banks;
  @Inject
  Dao<Merchant> merchants;
  @Inject
  Dao<Cardholder> cardholders;
  @Inject
  TransactionDao transactions;

  @Query
  @Description("Get all banks")
  public Set<Bank> getBanks() {

    return banks.getAll();
  }

  @Query
  @Description("Get all merchants")
  public Set<Merchant> getMerchants() {

    return merchants.getAll();
  }

  @Query
  @Description("Get all cardholders")
  public Set<Cardholder> getCardholders() {

    return cardholders.getAll();
  }

  @Query
  @Description("Get all cardholders with given last name")
  public Set<Cardholder> getCardholdersFromLastName(@Name("lastName") String lastName){

    return cardholders.getByName(lastName);
  }

  @Query
  @Description("Get all transactions")
  public Set<Transaction> getTransactions() {

    return transactions.getAll();
  }

  @Query
  @Description("Get cardholders and merchants from unsafe transactions")
  public Set<Transaction> getSuspectTransactions() {

    return transactions.getSuspect();
  }

  @Query
  @Description("Get all transactions with limit and offset")
  public Set<Transaction> getTransactionsLimitOffset(@Name("limit") int limit, @Name("offset") int offset){

    return transactions.getLimitOffset(limit, offset);
  }
}