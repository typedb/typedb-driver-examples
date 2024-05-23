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

package com.vaticle.typedb.example.telecom.phoneCalls;

import com.google.gson.stream.JsonReader;
import com.vaticle.typedb.driver.TypeDB;
import com.vaticle.typedb.driver.api.TypeDBDriver;
import com.vaticle.typedb.driver.api.TypeDBSession;
import com.vaticle.typedb.driver.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import mjson.Json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * reads a JSON encoded value as a stream of tokens,
 *
 * @see <a href="https://google.github.io/gson/apidocs/com/google/gson/stream/JsonReader.html">JsonReader</a>
 * <p>
 * a lean JSON Library for Java,
 * @see <a href="https://bolerio.github.io/mjson/">mjson</a>
 * <p>
 * a lean JSON Library for Java,
 * @see <a href="https://bolerio.github.io/mjson/">mjson</a>
 */
/**
 * a lean JSON Library for Java,
 * @see <a href="https://bolerio.github.io/mjson/">mjson</a>
 */

public class JSONMigration {
    /**
     * representation of Input object that links an input file to its own templating function,
     * which is used to map a Json object to a TypeQL query string
     */
    abstract static class Input {
        String path;

        public Input(String path) {
            this.path = path;
        }

        String getDataPath() {
            return path;
        }

        abstract String template(Json data);
    }

    public static void main(String[] args) throws IOException {
        String databaseName = (args[0] != null) ? args[0] : "phone_calls";
        Collection<Input> inputs = initialiseInputs();
        connectAndMigrate(inputs, databaseName);
    }

    /**
     * 1. creates a TypeDB instance
     * 2. creates a session to the targeted database
     * 3. initialises the list of Inputs, each containing details required to parse the data
     * 4. loads the csv data to TypeDB for each file
     * 5. closes the session
     * 6. closes the driver
     */
    static void connectAndMigrate(Collection<Input> inputs, String databaseName) throws IOException {
        TypeDBDriver driver = TypeDB.coreDriver("localhost:1729");
        TypeDBSession session = driver.session(databaseName, TypeDBSession.Type.DATA);

        for (Input input : inputs) {
            System.out.println("Loading from [" + input.getDataPath() + ".json] into TypeDB ...");
            loadDataIntoTypeDB(input, session);
        }

        session.close();
        driver.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        // define template for constructing a company TypeQL insert query
        inputs.add(new Input("telecom/phone_calls/data/companies") {
            @Override
            public String template(Json company) {
                return "insert $company isa company, has name " + company.at("name") + ";";
            }
        });
        // define template for constructing a person TypeQL insert query
        inputs.add(new Input("telecom/phone_calls/data/people") {
            @Override
            public String template(Json person) {
                // insert person
                String typeQLInsertQuery = "insert $person isa person, has phone-number " + person.at("phone_number");

                if (person.has("first_name")) {
                    typeQLInsertQuery += ", has first-name " + person.at("first_name");
                    typeQLInsertQuery += ", has last-name " + person.at("last_name");
                    typeQLInsertQuery += ", has city " + person.at("city");
                    typeQLInsertQuery += ", has age " + person.at("age").asInteger();
                }

                typeQLInsertQuery += ";";
                return typeQLInsertQuery;
            }
        });
        // define template for constructing a contract TypeQL insert query
        inputs.add(new Input("telecom/phone_calls/data/contracts") {
            @Override
            public String template(Json contract) {
                // match company
                String typeQLInsertQuery = "match $company isa company, has name " + contract.at("company_name") + ";";
                // match person
                typeQLInsertQuery += " $customer isa person, has phone-number " + contract.at("person_id") + ";";
                // insert contract
                typeQLInsertQuery += " insert (provider: $company, customer: $customer) isa contract;";
                return typeQLInsertQuery;
            }
        });
        // define template for constructing a call TypeQL insert query
        inputs.add(new Input("telecom/phone_calls/data/calls") {
            @Override
            public String template(Json call) {
                // match caller
                String typeQLInsertQuery = "match $caller isa person, has phone-number " + call.at("caller_id") + ";";
                // match callee
                typeQLInsertQuery += " $callee isa person, has phone-number " + call.at("callee_id") + ";";
                // insert call
                typeQLInsertQuery += " insert $call(caller: $caller, callee: $callee) isa call;" +
                        " $call has started-at " + call.at("started_at").asString() + ";" +
                        " $call has duration " + call.at("duration").asInteger() + ";";
                return typeQLInsertQuery;
            }
        });
        return inputs;
    }

    /**
     * loads the csv data into our TypeDB phone_calls database:
     * 1. gets the data items as a list of json objects
     * 2. for each json object
     * a. creates a TypeDB transaction
     * b. constructs the corresponding TypeQL insert query
     * c. runs the query
     * d. commits the transaction
     * e. closes the transaction
     *
     * @param input   contains details required to parse the data
     * @param session off of which a transaction is created
     * @throws IOException
     */
    static void loadDataIntoTypeDB(Input input, TypeDBSession session) throws IOException {
        ArrayList<Json> items = parseDataToJson(input); // 1
        for (Json item : items) {
            TypeDBTransaction transaction = session.transaction(TypeDBTransaction.Type.WRITE); // 2a
            String typeQLInsertQuery = input.template(item); // 2b
            System.out.println("Executing TypeQL Query: " + typeQLInsertQuery);
            transaction.query().insert(TypeQL.parseQuery(typeQLInsertQuery).asInsert()); // 2c
            transaction.commit(); // 2d

        }
        System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + ".json] into TypeDB.\n");
    }

    /**
     * 1. reads a json file through a stream
     * 2. parses each json object found in the file to a json object
     * 3. adds the json object to the list of items
     *
     * @param input used to get the path to the data file, minus the format
     * @return the list of json objects
     * @throws IOException
     */
    static ArrayList <Json> parseDataToJson(Input input) throws IOException {
        ArrayList <Json> items = new ArrayList <> ();

        JsonReader jsonReader = new JsonReader(getReader(input.getDataPath() + ".json")); // 1

        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            Json item = Json.object();
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                switch (jsonReader.peek()) {
                    case STRING:
                        item.set(key, jsonReader.nextString()); // 2
                        break;
                    case NUMBER:
                        item.set(key, jsonReader.nextInt()); // 2
                        break;
                }
            }
            jsonReader.endObject();
            items.add(item); // 3
        }
        jsonReader.endArray();
        return items;
    }

    public static Reader getReader(String relativePath) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(relativePath));
    }
}
