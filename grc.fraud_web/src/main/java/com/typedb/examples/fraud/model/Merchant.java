package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import java.util.Objects;

public class Merchant {

  @CsvBindByName(column = "merchant")
  private String name;

  @CsvBindByName(column = "category")
  private String category;

  @CsvRecurse
  private MerchantCoordinates coords;

  public Merchant() {}

  public Merchant(String name, String category, MerchantCoordinates coords) {

    this.name = name;
    this.category = category;
    this.coords = coords;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public MerchantCoordinates getCoords() {
    return coords;
  }

  public void setCoords(MerchantCoordinates coords) {
    this.coords = coords;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
        return true;
    }
    if (!(o instanceof Merchant merchant)) {
        return false;
    }

    return name.equals(merchant.name) && category.equals(merchant.category) &&
        coords.equals(merchant.coords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, category);
  }

  @Override
  public String toString() {

    return
        "merchant {" +
        "  name = '" + name + "', " +
        "  category = '" + category + "', " +
        "  coords = " + coords +
        "}";
  }
}
