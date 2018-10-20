package ai.grakn.examples;

import ai.grakn.GraknTxType;
import ai.grakn.Keyspace;
import ai.grakn.client.Grakn;
import ai.grakn.util.SimpleURI;

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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

public class XmlMigration {
    /**
     * representation of Input object that links an input file to its own templating function,
     * which is used to map a Json object to Graql query string
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
     * 4. loads the xml data to Grakn for each file
     * 5. closes the session
     */
    public static void main(String[] args) throws UnsupportedEncodingException, XMLStreamException {
        SimpleURI localGrakn = new SimpleURI("localhost", 48555);
        Keyspace keyspace = Keyspace.of("phone_calls");
        Grakn grakn = new Grakn(localGrakn);
        Grakn.Session session = grakn.session(keyspace);
        Collection<Input> inputs = initialiseInputs();


        inputs.forEach(input -> {
            System.out.println("Loading from [" + input.getDataPath() + "] into Grakn ...");
            try {
                loadDataIntoGrakn(input, session);
            } catch (UnsupportedEncodingException | XMLStreamException e) {
                e.printStackTrace();
            }
        });

        session.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        // Define template for companies file
        inputs.add(new Input("data/companies", "company") {
            @Override
            public String template(Json company) {
                return "insert $company isa company has name " + company.at("name") + ";";
            }
        });
        //Define template for people file
        inputs.add(new Input("data/people", "person") {
            @Override
            public String template(Json person) {
                // insert person
                String graqlInsertQuery = "insert $person isa person has phone-number " + person.at("phone_number");

                if (person.at("first_name").isNull()) {
                    // person is not a customer
                    graqlInsertQuery += " has is-customer false";
                } else {
                    // person is a customer
                    graqlInsertQuery += " has is-customer true";
                    graqlInsertQuery += " has first-name " + person.at("first_name");
                    graqlInsertQuery += " has last-name " + person.at("last_name");
                    graqlInsertQuery += " has city " + person.at("city");
                    graqlInsertQuery += " has age " + person.at("age").asInteger();
                }

                graqlInsertQuery += ";";
                return graqlInsertQuery;
            }
        });
        //Define template for contracts file
        inputs.add(new Input("data/contracts", "contract") {
            @Override
            public String template(Json contract) {
                // match company
                String graqlInsertQuery = "match $company isa company has name " + contract.at("company_name") + ";";
                // match person
                graqlInsertQuery += " $customer isa person has phone-number " + contract.at("person_id") + ";";
                // insert contract
                graqlInsertQuery += " insert (provider: $company, customer: $customer) isa contract;";
                return graqlInsertQuery;
            }
        });
        //Define template for calls file
        inputs.add(new Input("data/calls", "call") {
            @Override
            public String template(Json call) {
                // match caller
                String graqlInsertQuery = "match $caller isa person has phone-number " + call.at("caller_id") + ";";
                // match callee
                graqlInsertQuery += " $callee isa person has phone-number " + call.at("callee_id") + ";";
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
     * loads the xml data into our Grakn phone_calls keyspace:
     * 1. gets the data items as a list of json objects
     * 2. for each json object
     * a. creates a Grakn transaction
     * b. constructs the corresponding Graql insert query
     * c. runs the query
     * d. commits the transaction
     *
     * @param input   contains details required to parse the data
     * @param session off of which a transaction will be created
     * @throws UnsupportedEncodingException
     */
    static void loadDataIntoGrakn(Input input, Grakn.Session session) throws UnsupportedEncodingException, XMLStreamException {
        ArrayList<Json> items = parseDataToJson(input);
        items.forEach(item -> {
            Grakn.Transaction transaction = session.transaction(GraknTxType.WRITE);
            String graqlInsertQuery = input.template(item);
            System.out.println("Executing Graql Query: " + graqlInsertQuery);
            transaction.graql().parse(graqlInsertQuery).execute();
            transaction.commit();
        });
        System.out.println("\nInserted " + items.size() + " items from [" + input.getDataPath() + "] into Grakn.\n");
    }

    /**
     * 1. reads a xml file through a stream
     * 2. parses each tag to a json object
     * 3. adds the json object to the list of items
     *
     * @param input used to get the path to the data file (minus the format) and the tag selector
     * @return the list of json objects
     * @throws UnsupportedEncodingException
     */
    static ArrayList<Json> parseDataToJson(Input input) throws UnsupportedEncodingException, XMLStreamException {
        ArrayList<Json> items = new ArrayList<>();

        XMLStreamReader r = XMLInputFactory.newInstance().createXMLStreamReader(getReader(input.getDataPath() + ".xml"));
        String key;
        String value = null;
        Boolean inSelector = false;
        Json item = Json.object();

        while(r.hasNext()) {
            int event = r.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if (r.getLocalName().equals(input.getSelector())) {
                        inSelector = true;
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    value = r.getText();
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    key = r.getLocalName();
                    if (inSelector && ! key.equals(input.getSelector())) {
                        item.set(key, value);
                    }
                    if (key.equals(input.getSelector())) {
                        inSelector = false;
                        items.add(item);
                    }

                    break;
            }
        }

        return items;
    }

    public static Reader getReader(String relativePath) throws UnsupportedEncodingException {
        return new InputStreamReader(XmlMigration.class.getClassLoader().getResourceAsStream(relativePath), "UTF-8");
    }
}

