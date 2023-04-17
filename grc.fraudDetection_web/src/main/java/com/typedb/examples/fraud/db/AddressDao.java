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

package com.typedb.examples.fraud.db;

import com.typedb.examples.fraud.model.Address;
import java.util.Hashtable;

public class AddressDao {

  protected static Address fromResult(Hashtable<String, String> result) {

    var street = result.get("street");
    var city= result.get("city");
    var state = result.get("state");
    var zip = result.get("zip");

    var addr = new Address(street, city, state, zip);

    return addr;
  }
}
