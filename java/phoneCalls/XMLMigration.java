package grakn.example.phoneCalls;

import grakn.client.GraknClient;
import static graql.lang.Graql.*;
import graql.lang.query.GraqlInsert;

/**
 * a lean JSON Library for Java,
 * @see <a href="https://bolerio.github.io/mjson/">mjson</a>
 */
import mjson.Json;

/**
 * provides an easy and intuitive means of parsing and generating XML documents
 * @see <a href="https://docs.oracle.com/cd/E13222_01/wls/docs90/xml/stax.html">StAX</a>
 */
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class XMLMigration {
    /**
     * representation of Input object that links an input file to its own templating function,
     * which is used to map a Json object to a Graql query string
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

    /**
     * 1. creates a Grakn instance
     * 2. creates a session to the targeted keyspace
     * 3. initialises the list of Inputs, each containing details required to parse the data
     * 4. loads the csv data to Grakn for each file
     * 5. closes the session
     */
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        Collection<Input> inputs = initialiseInputs();
        connectAndMigrate(inputs);
    }

    static void connectAndMigrate(Collection<Input> inputs) throws FileNotFoundException, XMLStreamException {
        GraknClient client = new GraknClient("localhost:48555");
        GraknClient.Session session = client.session("phone_calls");

        for (Input input : inputs) {
            System.out.println("Loading from [" + input.getDataPath() + "] into Grakn ...");
            loadDataIntoGrakn(input, session);
        }

        session.close();
        client.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        // define template for constructing a company Graql insert query
        inputs.add(new Input("datasets/phone-calls/companies", "company") {
            @Override
            public String template(Json company) {
                return "insert $company isa company, has name " + company.at("name") + ";";
            }
        });
        // define template for constructing a person Graql insert query
        inputs.add(new Input("datasets/phone-calls/people", "person") {
            @Override
            public String template(Json person) {
                // insert person
                String graqlInsertQuery = "insert $person isa person, has phone-number " + person.at("phone_number");

                if (! person.has("first_name")) {
                    // person is not a customer
                    graqlInsertQuery += ", has is-customer false";
                } else {
                    // person is a customer
                    graqlInsertQuery += ", has is-customer true";
                    graqlInsertQuery += ", has first-name " + person.at("first_name");
                    graqlInsertQuery += ", has last-name " + person.at("last_name");
                    graqlInsertQuery += ", has city " + person.at("city");
                    graqlInsertQuery += ", has age " + person.at("age").asInteger();
                }

                graqlInsertQuery += ";";
                return graqlInsertQuery;
            }
        });
        // define template for constructing a contract Graql insert query
        inputs.add(new Input("datasets/phone-calls/contracts", "contract") {
            @Override
            public String template(Json contract) {
                // match company
                String graqlInsertQuery = "match $company isa company, has name " + contract.at("company_name") + ";";
                // match person
                graqlInsertQuery += " $customer isa person, has phone-number " + contract.at("person_id") + ";";
                // insert contract
                graqlInsertQuery += " insert (provider: $company, customer: $customer) isa contract;";
                return graqlInsertQuery;
            }
        });
        // define template for constructing a call Graql insert query
        inputs.add(new Input("datasets/phone-calls/calls", "call") {
            @Override
            public String template(Json call) {
                // match caller
                String graqlInsertQuery = "match $caller isa person, has phone-number " + call.at("caller_id") + ";";
                // match callee
                graqlInsertQuery += " $callee isa person, has phone-number " + call.at("callee_id") + ";";
                // insert call
                graqlInsertQuery += " insert $call(caller: $caller, callee: $callee) isa call;" +
                        " $call has started-at " + call.at("started_at").asString() + ";" +
                        " $call has duration " + call.at("duration").asInteger() + ";";
                return graqlInsertQuery;
            }
        });
        return inputs;
    }

    /**
     * loads the xml data into the Grakn phone_calls keyspace:
     * 1. gets the data items as a list of json objects
     * 2. for each json object:
     *   a. creates a Grakn transaction
     *   b. constructs the corresponding Graql insert query
     *   c. runs the query
     *   d. commits the transaction
     *
     * @param input   contains details required to parse the data
     * @param session off of which a transaction is created
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    static void loadDataIntoGrakn(Input input, GraknClient.Session session) throws FileNotFoundException, XMLStreamException {
        ArrayList<Json> items = parseDataToJson(input); // 1
        for (Json item : items) {
            GraknClient.Transaction transaction = session.transaction().write(); // 2a
            String graqlInsertQuery = input.template(item); // 2b
            System.out.println("Executing Graql Query: " + graqlInsertQuery);
            transaction.execute((GraqlInsert) parse(graqlInsertQuery)); // 2c
            transaction.commit(); // 2d

        }
        System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + "] into Grakn.\n");
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