package grakn.example.xcom;

import grakn.client.GraknClient;
import graql.lang.Graql;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlGet;
import org.junit.*;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static graql.lang.Graql.parse;
import static org.junit.Assert.assertEquals;


public class XCOMTest {

	GraknClient client;
	GraknClient.Session session;
	String keyspaceName = "xcom_test";

	@Before
	public void loadSchema() {
		client = new GraknClient("localhost:48555");
		session = client.session(keyspaceName);
		GraknClient.Transaction transaction = session.transaction().write();

		try {
			byte[] encoded = Files.readAllBytes(Paths.get("schemas/phone-calls-schema.gql"));
			String query = new String(encoded, StandardCharsets.UTF_8);
			transaction.execute((GraqlDefine) Graql.parse(query));
			transaction.commit();
			System.out.println("Loaded the " + keyspaceName + " schema");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCSVMigration() throws FileNotFoundException {
		Migration.main(new String[] { keyspaceName });
		assertMigrationResults();
	}

	@Test
	public void testQueries() throws FileNotFoundException {
		/*List<Queries.QueryExample> queryExamples = Queries.getTestSubjects();

		Queries.processSelection(0, queryExamples, keyspaceName);*/

		Migration.main(new String[]{ keyspaceName });

		GraknClient.Transaction transaction = session.transaction().read();

		transaction.close();
	}


	public void assertMigrationResults() {
		GraknClient.Transaction transaction = session.transaction().read();

		Number numberOfPeople = transaction.execute((GraqlGet.Aggregate) parse("match $x isa person; get $x; count;")).get(0).number().intValue();
		assertEquals(numberOfPeople, 30);

		Number numberOfCompanies = transaction.execute((GraqlGet.Aggregate) parse("match $x isa company; get $x; count;")).get(0).number().intValue();
		assertEquals(numberOfCompanies, 1);

		Number numberOfContracts = transaction.execute((GraqlGet.Aggregate) parse("match $x isa contract; get $x; count;")).get(0).number().intValue();
		assertEquals(numberOfContracts, 10);

		Number numberOfCalls = transaction.execute((GraqlGet.Aggregate) parse("match $x isa call; get $x; count;")).get(0).number().intValue();
		assertEquals(numberOfCalls, 200);

		transaction.close();
	}

	@After
	public void deleteKeyspace() {
		client.keyspaces().delete(keyspaceName);
		System.out.println("Deleted the " + keyspaceName + " keyspace");
		session.close();
		client.close();
	}
}
