/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.typedb.examples.fraudDetection.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import java.util.Objects;

public class Transaction {

  @CsvBindByName(column = "amt")
  private String amount;

  @CsvBindByName(column = "trans_num")
  private String number;

  @CsvBindByName(column = "trans_date_trans_time")
  private String time;

  @CsvRecurse
  private Merchant merchant;

  @CsvRecurse
  private Cardholder cardholder;

  public Transaction() {}

  public Transaction(String amount, String number, String time,
      Merchant merchant, Cardholder cardholder) {

    this.amount = amount;
    this.number = number;
    this.time = time;
    this.merchant = merchant;
    this.cardholder = cardholder;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Cardholder getCardholder() {
    return cardholder;
  }

  public void setCardholder(Cardholder cardholder) {
    this.cardholder = cardholder;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
        return true;
    }
    if (!(o instanceof Transaction that)) {
        return false;
    }

    return amount.equals(that.amount) && number.equals(that.number) && time.equals(that.time) &&
        merchant.equals(that.merchant) && cardholder.equals(that.cardholder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, number, time, merchant, cardholder);
  }

  @Override
  public String toString() {
    return "Transaction{" +
            "amount='" + amount + '\'' +
            ", number='" + number + '\'' +
            ", time='" + time + '\'' +
            ", merchant=" + merchant +
            ", cardholder=" + cardholder +
            '}';
  }
}
