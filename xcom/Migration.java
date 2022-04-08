package com.vaticle.typedb.example.xcom;

import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;


/**
 * a collection of fast and reliable Java-based parsers for CSV, TSV and Fixed Width files
 *
 * @see <a href="https://www.univocity.com/pages/univocity_parsers_documentation">univocity</a>
 * <p>
 * a lean JSON Library for Java,
 * @see <a href="https://bolerio.github.io/mjson/">mjson</a>
 */
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * a lean JSON Library for Java,
 * @see <a href="https://bolerio.github.io/mjson/">mjson</a>
 */
import mjson.Json;

public class Migration {
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
        String keyspaceName = (args != null && args.length > 0 && args[0] != null) ? args[0] : "xcom";
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
    static void connectAndMigrate(Collection<Input> inputs, String keyspaceName) throws FileNotFoundException {
        TypeDBClient client = TypeDB.coreClient("localhost:1729");
        TypeDBSession session = client.session(keyspaceName, TypeDBSession.Type.DATA);

        for (Input input : inputs) {
            System.out.println("Loading from [" + input.getDataPath() + ".csv] into TypeDB ...");
            loadDataIntoTypeDB(input, session);
        }

        session.close();
        client.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        inputs.add(initialiseTechInput());
        inputs.add(initialiseItemInput());
        inputs.add(initialiseResearchProjectTechRequirementInput());
        inputs.add(initialiseResearchResourceCostInput());

        return inputs;
    }

    /** define template for constructing a research project TypeQL insert query */
    static Input initialiseTechInput() {
        return new Input("datasets/xcom/tech") {
            @Override
            public String template(Json researchProject) {
                return "insert $research_project isa research-project, has name " + researchProject.at("name") + ";";
            }
        };
    }

    /** define template for constructing a research project tech requirement TypeQL insert query */
    static Input initialiseResearchProjectTechRequirementInput() {
        return new Input("datasets/xcom/tech_required_tech") {
            @Override
            public String template(Json techRequirement) {
                // match tech
                String typeQLInsertQuery = "match $tech isa research-project, has name " + techRequirement.at("tech") + ";";
                // match required tech
                typeQLInsertQuery += " $required_tech isa research-project, has name " + techRequirement.at("required_tech") + ";";
                // insert research project tech requirement
                typeQLInsertQuery += " insert (research-to-begin: $tech, required-tech: $required_tech) isa tech-requirement-to-begin-research;";
                return typeQLInsertQuery;
            }
        };
    }

    /** define template for constructing an item TypeQL insert query */
    static Input initialiseItemInput() {
        return new Input("datasets/xcom/resource") {
            @Override
            public String template(Json item) {
                return "insert $item isa item, has name " + item.at("name") + ";";
            }
        };
    }

    /** define template for constructing a research project resource cost TypeQL insert query */
    static Input initialiseResearchResourceCostInput() {
        return new Input("datasets/xcom/tech_required_resource") {
            @Override
            public String template(Json researchCost) {
                // match tech
                String typeQLInsertQuery = "match $tech isa research-project, has name " + researchCost.at("tech") + ";";
                // match required tech
                typeQLInsertQuery += " $item isa item, has name " + researchCost.at("required_resource") + ";";
                // insert research project tech requirement
                typeQLInsertQuery += " insert (research-to-begin: $tech, consumes-resource: $item) isa resource-cost-to-begin-research,"
                    + " has quantity-consumed " + researchCost.at("required_resource_count").asLong() + ";";
                return typeQLInsertQuery;
            }
        };
    }

    /**
     * loads the csv data into our TypeDB xcom keyspace:
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
