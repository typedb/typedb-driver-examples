package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.CardholderCoordinates;
import java.util.Hashtable;

public class CardholderCoordsDao {

  protected static CardholderCoordinates fromResult(Hashtable<String, String> result) {

    var lat = result.get("cardholderLat");
    var lon = result.get("cardholderLon");

    var coords = new CardholderCoordinates(lat, lon);

    return coords;
  }
}
