package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.db.TypeDbSessionWrapper;
import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.BankCoordinates;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class BankDao {

  private static final String INSERT_QUERY_TEMPLATE =
      "insert " +
      "  $bank isa Bank, has name \"%s\", has company_type \"Bank\";" +
      "  $bankCoords isa Geo_coordinate, has latitude %s, has longitude %s;" +
      "  $bankGeo (geo: $bankCoords, identify: $bank) isa geolocate;";

  protected static final String BANK_MATCH =
      "  $bank isa Bank, has name $bankName;" +
      "  $bankCoords isa Geo_coordinate, has latitude $bankLat, has longitude $bankLon;" +
      "  $bankGeo (geo: $bankCoords, identify: $bank) isa geolocate;";

  @Inject
  TypeDbSessionWrapper db;

  public Set<Bank> getAll() {

    var results = db.getAll("match " + BANK_MATCH);

    var banks = results.stream().map(BankDao::fromResult).collect(Collectors.toSet());

    return banks;
  }

  public void insertAll(Set<Bank> banks) {

    var queries = banks.stream().map(this::getInsertQueryStr).collect(Collectors.toSet());

    db.insertAll(queries);
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
