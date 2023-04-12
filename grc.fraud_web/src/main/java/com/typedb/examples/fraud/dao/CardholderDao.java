package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.db.TypeDbSessionWrapper;
import com.typedb.examples.fraud.model.Cardholder;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CardholderDao {

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
  TypeDbSessionWrapper db;

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
