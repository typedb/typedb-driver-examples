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

package com.typedb.examples.fraudDectection.model;

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
