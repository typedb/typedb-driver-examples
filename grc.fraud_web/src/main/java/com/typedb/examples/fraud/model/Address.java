package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

public class Address {
  @CsvBindByName(column = "street")
  private String street;
  @CsvBindByName(column = "city")
  private String city;
  @CsvBindByName(column = "state")
  private String state;
  @CsvBindByName(column = "zip")
  private String zip;

  public Address() {}

  public Address(String street, String city, String state, String zip) {

    this.street = street;
    this.city = city;
    this.state = state;
    this.zip = zip;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Address address)) {
      return false;
    }
    return street.equals(address.street) && city.equals(address.city) && state.equals(address.state)
        && zip.equals(address.zip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, city, state, zip);
  }

  @Override
  public String toString() {

    return
        "Address {" +
        "  street = '" + street + "', " +
        "  city = '" + city + "', " +
        "  state = '" + state + "', " +
        "  zip = '" + zip + "'" +
        "}";
  }
}
