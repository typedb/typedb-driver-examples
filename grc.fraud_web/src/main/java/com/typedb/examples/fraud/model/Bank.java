package com.typedb.examples.fraud.model;

import java.util.Objects;

public class Bank {

  private String name;
  private BankCoordinates coords;

  public Bank() {}

  public Bank(String name, BankCoordinates coords) {

    this.name = name;
    this.coords = coords;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BankCoordinates getCoords() {
    return coords;
  }

  public void setCoords(BankCoordinates coords) {
    this.coords = coords;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Bank bank)) {
      return false;
    }

    return name.equals(bank.name) && coords.equals(bank.coords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, coords);
  }

  @Override
  public String toString() {

    return
        "bank {" +
        "  name = '" + name + "', " +
        "  coords = " + coords +
        "}";
  }
}
