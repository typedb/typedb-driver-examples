package com.vaticle.typedb.example.phoneCalls;

import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import mjson.Json;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

public class XMLMigration {
    /**
     * representation of Input object that links an input file to its own templating function,
     * which is used to map a Json object to a TypeQL query string
     */
    abstract static class Input {
        String path;
        String selector;

        public Input(String path, String selector) {
            this.path = path;
            this.selector = selector;
        }

        String getDataPath(){ return path;}
        String getSelector(){ return selector;}

        abstract String template(Json data);
    }

    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        String keyspaceName = (args[0] != null) ? args[0] : "phone_calls";
        Collection<Input> inputs = initialiseInputs();
        connectAndMigrate(inputs, keyspaceName);
    }

    /**
     * 1. creates a TypeDB instance
     * 2. creates a session to the targeted keyspace
     * 3. initialises the list of Inputs, each containing details required to parse the data
     * 4. loads the csv data to TypeDB for each file
     * 5. closes the session
     * 6. closes the client
     */
    static void connectAndMigrate(Collection<Input> inputs, String keyspaceName) throws FileNotFoundException, XMLStreamException {
        TypeDBClient client = TypeDB.coreClient("localhost:1729");
        TypeDBSession session = client.session(keyspaceName, TypeDBSession.Type.DATA);

        for (Input input : inputs) {
            System.out.println("Loading from [" + input.getDataPath() + ".xml] into TypeDB ...");
            loadDataIntoTypeDB(input, session);
        }

        session.close();
        client.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        // define template for constructing a company TypeQL insert query
        inputs.add(new Input("datasets/phone-calls/companies", "company") {
            @Override
            public String template(Json company) {
                return "insert $company isa company, has name " + company.at("name") + ";";
            }
        });
        // define template for constructing a person TypeQL insert query
        inputs.add(new Input("datasets/phone-calls/people", "person") {
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
        inputs.add(new Input("datasets/phone-calls/contracts", "contract") {
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
        inputs.add(new Input("datasets/phone-calls/calls", "call") {
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
     * loads the xml data into the TypeDB phone_calls keyspace:
     * 1. gets the data items as a list of json objects
     * 2. for each json object:
     *   a. creates a TypeDB transaction
     *   b. constructs the corresponding TypeQL insert query
     *   c. runs the query
     *   d. commits the transaction
     *
     * @param input   contains details required to parse the data
     * @param session off of which a transaction is created
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    static void loadDataIntoTypeDB(Input input, TypeDBSession session) throws FileNotFoundException, XMLStreamException {
        ArrayList<Json> items = parseDataToJson(input); // 1
        for (Json item : items) {
            TypeDBTransaction transaction = session.transaction(TypeDBTransaction.Type.WRITE); // 2a
            String typeQLInsertQuery = input.template(item); // 2b
            System.out.println("Executing TypeQL Query: " + typeQLInsertQuery);
            transaction.query().insert(TypeQL.parseQuery(typeQLInsertQuery).asInsert()); // 2c
            transaction.commit(); // 2d

        }
        System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + ".xml] into TypeDB.\n");
    }

    /**
     * 1. reads a xml file through a stream
     * 2. parses each tag to a json object
     * 3. adds the json object to the list of items
     *
     * @param input used to get the path to the data file (minus the format) and the tag selector
     * @return the list of json objects
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    static ArrayList <Json> parseDataToJson(Input input) throws FileNotFoundException, XMLStreamException {
        ArrayList <Json> items = new ArrayList <> ();

        XMLStreamReader r = XMLInputFactory.newInstance().createXMLStreamReader(getReader(input.getDataPath() + ".xml")); // 1
        String key;
        String value = null;
        Boolean inSelector = false;
        Json item = null;
        while (r.hasNext()) {
            int event = r.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if (r.getLocalName().equals(input.getSelector())) {
                        inSelector = true;
                        item = Json.object();
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    value = r.getText();
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    key = r.getLocalName();
                    if (inSelector && !key.equals(input.getSelector())) {
                        item.set(key, value); // 2
                    }
                    if (key.equals(input.getSelector())) {
                        inSelector = false;
                        items.add(item); // 3
                    }

                    break;
            }
        }

        return items;
    }

    public static Reader getReader(String relativePath) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(relativePath));
    }
}