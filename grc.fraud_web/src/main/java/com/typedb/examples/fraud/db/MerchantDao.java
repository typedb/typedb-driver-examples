package com.typedb.examples.fraud.db;

import com.typedb.examples.fraud.model.Merchant;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class MerchantDao implements StandardDao<Merchant> {

  private static final String INSERT_QUERY_TEMPLATE =
      "insert " +
      "  $merchant isa Company, has name \"%s\", has company_type \"%s\";" +
      "  $merchantCoords isa Geo_coordinate, has latitude %s, has longitude %s;" +
      "  $merchantGeo (geo: $merchantCoords, identify: $merchant) isa geolocate;";

  protected static final String MERCHANT_MATCH =
      "  $merchant isa Company, has name $merchantName, has company_type $merchantType;" +
      "  $merchantCoords isa Geo_coordinate, has latitude $merchantLat, has longitude $merchantLon;" +
      "  $merchantGeo (geo: $merchantCoords, identify: $merchant) isa geolocate;";

  @Inject
  TypeDBSessionWrapper db;

  public Set<Merchant> getAll() {

    var results = db.getAll("match " + MERCHANT_MATCH);

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
