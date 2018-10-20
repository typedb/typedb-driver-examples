package ai.grakn.examples;

import ai.grakn.GraknTxType;
import ai.grakn.Keyspace;
import ai.grakn.client.Grakn;
import ai.grakn.util.SimpleURI;

/**
 * reads a JSON encoded value as a stream of tokens,
 * @see <a href="https://google.github.io/gson/apidocs/com/google/gson/stream/JsonReader.html">JsonReader</a>
 */
import com.google.gson.stream.JsonReader;

/**
 * a lean JSON Library for Java,
 * @see <a href="https://bolerio.github.io/mjson/">mjson</a>
 */
import mjson.Json;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class JsonMigration {
    /**
     * representation of Input object that links an input file to its own templating function,
     * which is used to map a Json object to Graql query string
     */
    abstract static class Input {
        String path;

        public Input(String path) {
            this.path = path;
        }

        String getDataPath(){ return path;}

        abstract String template(Json data);
    }

    /**
     * 1. creates a Grakn instance
     * 2. creates a session to the targeted keyspace
     * 3. initialises the list of Inputs, each containing details required to parse the data
     * 4. loads the json data to Grakn for each file
     * 5. closes the session
     */
    public static void main(String[] args) {
        SimpleURI localGrakn = new SimpleURI("localhost", 48555);
        Keyspace keyspace = Keyspace.of("phone_calls");
        Grakn grakn = new Grakn(localGrakn);
        Grakn.Session session = grakn.session(keyspace);
        Collection<Input> inputs = initialiseInputs();


        inputs.forEach(input -> {
            System.out.println("Loading from [" + input.getDataPath() + "] into Grakn ...");
            try {
                loadDataIntoGrakn(input, session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        session.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        // Define template for companies file
        inputs.add(new Input("data/companies") {
            @Override
            public String template(Json company) {
                return "insert $company isa company has name " + company.at("name") + ";";
            }
        });
        //Define template for people file
        inputs.add(new Input("data/people") {
            @Override
            public String template(Json person) {
                // insert person
                String graqlInsertQuery = "insert $person isa person has phone-number " + person.at("phone_number");

                if (! person.has("first_name")) {
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
        inputs.add(new Input("data/contracts") {
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
        inputs.add(new Input("data/calls") {
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
     * loads the json data into our Grakn phone_calls keyspace:
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
    static void loadDataIntoGrakn(Input input, Grakn.Session session) throws IOException {
        ArrayList<Json> items = parseDataToJson(input);
        items.forEach(item -> {
            Grakn.Transaction transaction = session.transaction(GraknTxType.WRITE);
            String graqlInsertQuery = input.template(item);
            System.out.println("Executing Graql Query: " + graqlInsertQuery);
            transaction.graql().parse(graqlInsertQuery).execute();
            transaction.commit();
        });
        System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + "] into Grakn.\n");
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
    static ArrayList<Json> parseDataToJson(Input input) throws IOException {
        ArrayList<Json> items = new ArrayList<>();

        JsonReader jsonReader = new JsonReader(getReader(input.getDataPath() + ".json"));

        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            Json item = Json.object();
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                switch (jsonReader.peek()) {
                    case STRING:
                        item.set(key, jsonReader.nextString());
                        break;
                    case NUMBER:
                        item.set(key, jsonReader.nextInt());
                        break;
                }
            }
            jsonReader.endObject();
            items.add(item);
        }
        jsonReader.endArray();
        return items;
    }

    public static Reader getReader(String relativePath) throws UnsupportedEncodingException {
        return new InputStreamReader(JsonMigration.class.getClassLoader().getResourceAsStream(relativePath), "UTF-8");
    }
}