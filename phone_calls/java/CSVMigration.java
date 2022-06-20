package com.vaticle.typedb.example.phoneCalls;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import mjson.Json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

public class CSVMigration {
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

    public static void main(String[] args) throws FileNotFoundException {
        String databaseName = (args != null && args.length > 0 && args[0] != null) ? args[0] : "phone_calls";
        Collection<Input> inputs = initialiseInputs();
        connectAndMigrate(inputs, databaseName);
    }

    /**
     * 1. creates a TypeDB instance
     * 2. creates a session to the targeted database
     * 3. initialises the list of Inputs, each containing details required to parse the data
     * 4. loads the csv data to TypeDB for each file
     * 5. closes the session
     * 6. closes the client
     */
    static void connectAndMigrate(Collection<Input> inputs, String databaseName) throws FileNotFoundException {
        TypeDBClient client = TypeDB.coreClient("localhost:1729");
        TypeDBSession session = client.session(databaseName, TypeDBSession.Type.DATA);

        for (Input input : inputs) {
            System.out.println("Loading from [" + input.getDataPath() + ".csv] into TypeDB ...");
            loadDataIntoTypeDB(input, session);
        }

        session.close();
        client.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        // define template for constructing a company TypeQL insert query
        inputs.add(new Input("datasets/phone-calls/companies") {
            @Override
            public String template(Json company) {
                return "insert $company isa company, has name " + company.at("name") + ";";
            }
        });

        // define template for constructing a person TypeQL insert query
        inputs.add(new Input("datasets/phone-calls/people") {
            @Override
            public String template(Json person) {
                // insert person
                String typeQLInsertQuery = "insert $person isa person, has phone-number " + person.at("phone_number");

                if (! person.at("first_name").isNull()) {
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
        inputs.add(new Input("datasets/phone-calls/contracts") {
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
        inputs.add(new Input("datasets/phone-calls/calls") {
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
     * @throws FileNotFoundException
     */
    static void loadDataIntoTypeDB(Input input, TypeDBSession session) throws FileNotFoundException {
        ArrayList<Json> items = parseDataToJson(input); // 1
        for (Json item : items) {
            TypeDBTransaction transaction = session.transaction(TypeDBTransaction.Type.WRITE); // 2a
            String typeQLInsertQuery = input.template(item); // 2b
            System.out.println("Executing TypeQL Query: " + typeQLInsertQuery);
            transaction.query().insert(TypeQL.parseQuery(typeQLInsertQuery).asInsert()); // 2c
            transaction.commit(); // 2d
        }
        System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + ".csv] into TypeDB.\n");
    }

    /**
     * 1. reads a csv file through a stream
     * 2. parses each row to a json object
     * 3. adds the json object to the list of items
     *
     * @param input used to get the path to the data file, minus the format
     * @return the list of json objects
     * @throws FileNotFoundException
     */
    static ArrayList<Json> parseDataToJson(Input input) throws FileNotFoundException {
        ArrayList<Json> items = new ArrayList<>();

        CsvParserSettings settings = new CsvParserSettings();
        settings.setLineSeparatorDetectionEnabled(true);
        CsvParser parser = new CsvParser(settings);
        parser.beginParsing(getReader(input.getDataPath() + ".csv")); // 1

        String[] columns = parser.parseNext();
        String[] row;
        while ((row = parser.parseNext()) != null) {
            Json item = Json.object();
            for (int i = 0; i < row.length; i++) {
                item.set(columns[i], row[i]); // 2
            }
            items.add(item); // 3
        }
        return items;
    }

    public static Reader getReader(String relativePath) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(relativePath));
    }
}
