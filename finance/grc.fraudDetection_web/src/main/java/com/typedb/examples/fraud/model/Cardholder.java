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

package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import java.util.Objects;

public class Cardholder {

  @CsvBindByName(column = "first")
  private String firstName;
  @CsvBindByName(column = "last")
  private String lastName;
  @CsvBindByName(column = "gender")
  private String gender;
  @CsvBindByName(column = "job")
  private String job;
  @CsvBindByName(column = "dob")
  private String birthDate;

  @CsvRecurse
  private Address address;
  @CsvRecurse
  private CardholderCoordinates coords;
  @CsvRecurse
  private CreditCard creditCard;

  public Cardholder() {}

  public Cardholder(String firstName, String lastName, String gender, String job, String birthDate,
      Address address, CardholderCoordinates coords, CreditCard creditCard) {

    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.job = job;
    this.birthDate = birthDate;
    this.address = address;
    this.coords = coords;
    this.creditCard = creditCard;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getJob() {
    return job;
  }

  public void setJob(String job) {
    this.job = job;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public CardholderCoordinates getCoords() {
    return coords;
  }

  public void setCoords(CardholderCoordinates coords) {
    this.coords = coords;
  }

  public CreditCard getCreditCard() {
    return creditCard;
  }

  public void setCreditCard(CreditCard creditCard) {
    this.creditCard = creditCard;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
        return true;
    }
    if (!(o instanceof Cardholder that)) {
        return false;
    }

    return firstName.equals(that.firstName) && lastName.equals(that.lastName) &&
        gender.equals(that.gender) && job.equals(that.job) && birthDate.equals(that.birthDate) &&
        address.equals(that.address) && coords.equals(that.coords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "Cardholder{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", gender='" + gender + '\'' +
            ", job='" + job + '\'' +
            ", birthDate='" + birthDate + '\'' +
            ", address=" + address +
            ", coords=" + coords +
            ", cc=" + creditCard +
            '}';
  }
}
