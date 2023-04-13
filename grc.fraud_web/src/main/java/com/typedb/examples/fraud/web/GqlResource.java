package com.typedb.examples.fraud.web;

import com.typedb.examples.fraud.db.StandardDao;
import com.typedb.examples.fraud.db.TransactionDao;
import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.Merchant;
import com.typedb.examples.fraud.model.Transaction;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class GqlResource {

  @Inject
  StandardDao<Bank> banks;
  @Inject
  StandardDao<Merchant> merchants;
  @Inject
  StandardDao<Cardholder> cardholders;
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
  @Description("Get all transactions")
  public Set<Transaction> getTransactions() {

    return transactions.getAll();
  }

  @Query
  @Description("Get cardholders and merchants from unsafe transactions")
  public Set<Transaction> getSuspectTransactions() {

    return transactions.getSuspect();
  }
}