package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.Address;
import java.util.Hashtable;

public class AddressDao {

  protected static Address fromResult(Hashtable<String, String> result) {

    var street = result.get("street");
    var city= result.get("city");
    var state = result.get("state");
    var zip = result.get("zip");

    var addr = new Address(street, city, state, zip);

    return addr;
  }
}
