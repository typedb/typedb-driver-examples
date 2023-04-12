package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.db.TypeDbSessionWrapper;
import com.typedb.examples.fraud.model.Transaction;
import com.typedb.examples.fraud.util.Formatter;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class TransactionDao {

  private static final String PERSON_CARD_MATCH =
      "match " +
      "  $cc isa Card, has card_number %s;" +
      "  $merchant isa Company, has name \"%s\";";

  private static final String INSERT_QUERY_TEMPLATE =
      "insert " +
      "  (used_card: $cc ,to: $merchant) isa transaction, has timestamp %s, has amount %s, has transaction_number \"%s\";";

  private static final String TX_MATCH =
      "  $tx (used_card: $cc ,to: $merchant) isa transaction, has timestamp $txTime, has amount $txAmount, has transaction_number $txNum;";

  private static final String SUSPECT_TX_MATCH =
      "  $suspect (unsafe_buyer: $cardholder, unsafe_company: $merchant) isa unsafe_relationship;";
  @Inject
  TypeDbSessionWrapper db;

  public TransactionDao(TypeDbSessionWrapper db) {
    this.db = db;
  }

  public Set<Transaction> getAll() {

    return getAll(false);
  }

  public Set<Transaction> getSuspect() {

    return getAll(true);
  }

  private Set<Transaction> getAll(boolean suspect) {

    var getQueryStr =
        "match " + TX_MATCH + CardholderDao.CARDHOLDER_MATCH + BankDao.BANK_MATCH + MerchantDao.MERCHANT_MATCH;

    if (suspect) {
      getQueryStr += SUSPECT_TX_MATCH;
    }

    var results = db.getAll(getQueryStr);

    var transactions = results.stream().map(TransactionDao::fromResult).collect(Collectors.toSet());

    return transactions;
  }

  public void insertAll(Set<Transaction> transactions) {

    var queries = transactions.stream().map(this::getInsertQueryStr).collect(Collectors.toSet());

    db.insertAll(queries);
  }

  protected static Transaction fromResult(Hashtable<String, String> result) {

    var merchant = MerchantDao.fromResult(result);
    var cardholder = CardholderDao.fromResult(result);

    var txAmount = result.get("txAmount");
    var txNum = result.get("txNum");
    var txTime = result.get("txTime");

    var tx = new Transaction(txAmount, txNum, txTime, merchant, cardholder);

    return tx;
  }

  private String getInsertQueryStr(Transaction transaction) {

    var match = PERSON_CARD_MATCH.formatted(
        transaction.getCardholder().getCc().getNumber(),
        transaction.getMerchant().getName()
    );

    var insert = INSERT_QUERY_TEMPLATE.formatted(
        Formatter.formatDateTime(transaction.getTime()),
        transaction.getAmount(),
        transaction.getNumber()
    );

    var insertQuery = match + insert;

    return insertQuery;
  }
}

