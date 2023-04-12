package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.MerchantCoordinates;
import java.util.Hashtable;

public class MerchantCoordsDao {

  protected static MerchantCoordinates fromResult(Hashtable<String, String> result) {

    var lat = result.get("merchantLat");
    var lon = result.get("merchantLon");

    var coords = new MerchantCoordinates(lat, lon);

    return coords;
  }
}
