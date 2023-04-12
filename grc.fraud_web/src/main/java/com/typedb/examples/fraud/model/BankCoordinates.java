package com.typedb.examples.fraud.model;

import java.util.Objects;

public class BankCoordinates {

  private String latitude;
  private String longitude;

  public BankCoordinates() {}

  public BankCoordinates(String latitude, String longitude) {

    this.latitude = latitude;
    this.longitude = longitude;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof BankCoordinates that)) {
      return false;
    }

    return latitude.equals(that.latitude) && longitude.equals(that.longitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitude, longitude);
  }

  @Override
  public String toString() {
    return
        "coordinates {" +
        "  latitude = '" + latitude + "'," +
        "  longitude = '" + longitude + "'" +
        "}";
  }
}