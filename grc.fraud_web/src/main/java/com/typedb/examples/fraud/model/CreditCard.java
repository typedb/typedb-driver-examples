package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

public class CreditCard {

  @CsvBindByName(column = "cc_num")
  private String number;

  private Bank bank;

  public CreditCard() {}

  public CreditCard(String number, Bank bank) {

    this.number = number;
    this.bank = bank;
  }

  public String getNumber() {
    return number;
  }

  public Bank getBank() {
    return bank;
  }

  public void setBank(Bank bank) {
    this.bank = bank;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
        return true;
    }
    if (!(o instanceof CreditCard that)) {
        return false;
    }

    return number.equals(that.number) && bank.equals(that.bank);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, bank);
  }

  @Override
  public String toString() {

    return
        "creditCard {" +
        "  number = '" + number + "', " +
        "  bank = '" + bank + "'" +
        "}";
  }
}
