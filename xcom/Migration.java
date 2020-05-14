package grakn.example.xcom;

import grakn.client.GraknClient;
import graql.lang.query.GraqlInsert;
import static graql.lang.Graql.parse;

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
     * which is used to map a Json object to a Graql query string
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
        String keyspaceName = (args[0] != null) ? args[0] : "xcom";
        Collection<Input> inputs = initialiseInputs();
        connectAndMigrate(inputs, keyspaceName);
    }

    /**
     * 1. creates a Grakn instance
     * 2. creates a session to the targeted keyspace
     * 3. initialises the list of Inputs, each containing details required to parse the data
     * 4. loads the csv data to Grakn for each file
     * 5. closes the session
     * 6. closes the client
     */
    static void connectAndMigrate(Collection<Input> inputs, String keyspaceName) throws FileNotFoundException {
        GraknClient client = new GraknClient("localhost:48555");
        GraknClient.Session session = client.session(keyspaceName);

        for (Input input : inputs) {
            System.out.println("Loading from [" + input.getDataPath() + ".csv] into Grakn ...");
            loadDataIntoGrakn(input, session);
        }

        session.close();
        client.close();
    }

    static Collection<Input> initialiseInputs() {
        Collection<Input> inputs = new ArrayList<>();

        // define template for constructing a research project Graql insert query
        inputs.add(new Input("datasets/xcom/tech") {
            @Override
            public String template(Json researchProject) {
                return "insert $research_project isa research-project, has name " + researchProject.at("name") + ";";
            }
        });

        // define template for constructing a research project tech requirement Graql insert query
        inputs.add(new Input("datasets/xcom/tech_required_tech") {
            @Override
            public String template(Json techRequirement) {
                // match tech
                String graqlInsertQuery = "match $tech isa research-project, has name " + techRequirement.at("tech") + ";";
                // match required tech
                graqlInsertQuery += " $required_tech isa research-project, has name " + techRequirement.at("required_tech") + ";";
                // insert research project tech requirement
                graqlInsertQuery += " insert (research-to-begin: $tech, required-tech: $required_tech) isa tech-requirement-to-begin-research;";
                return graqlInsertQuery;
            }
        });

        return inputs;
    }

    /**
     * loads the csv data into our Grakn xcom keyspace:
     * 1. gets the data items as a list of json objects
     * 2. for each json object
     * a. creates a Grakn transaction
     * b. constructs the corresponding Graql insert query
     * c. runs the query
     * d. commits the transaction
     * e. closes the transaction
     *
     * @param input   contains details required to parse the data
     * @param session off of which a transaction is created
     * @throws FileNotFoundException
     */
    static void loadDataIntoGrakn(Input input, GraknClient.Session session) throws FileNotFoundException {
        ArrayList<Json> items = parseDataToJson(input); // 1
        for (Json item : items) {
            GraknClient.Transaction transaction = session.transaction().write(); // 2a
            String graqlInsertQuery = input.template(item); // 2b
            System.out.println("Executing Graql Query: " + graqlInsertQuery);
            transaction.execute((GraqlInsert) parse(graqlInsertQuery)); // 2c
            transaction.commit(); // 2d
        }
        System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + ".csv] into Grakn.\n");
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
