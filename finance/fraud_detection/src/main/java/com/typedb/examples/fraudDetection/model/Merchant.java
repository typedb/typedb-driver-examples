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
