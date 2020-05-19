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
import java.util.*;

import static graql.lang.Graql.parse;
import static org.junit.Assert.*;


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
			byte[] encoded = Files.readAllBytes(Paths.get("schemas/xcom-schema.gql"));
			String query = new String(encoded, StandardCharsets.UTF_8);
			transaction.execute((GraqlDefine) Graql.parse(query));
			transaction.commit();
			System.out.println("Loaded the " + keyspaceName + " schema");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void deleteKeyspace() {
		client.keyspaces().delete(keyspaceName);
		System.out.println("Deleted the " + keyspaceName + " keyspace");
		session.close();
		client.close();
	}

	@Test
	public void testMigration() throws FileNotFoundException {
		Migration.main(new String[] { keyspaceName });
		assertMigrationResults();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testQueries() throws FileNotFoundException {
		Migration.main(new String[]{ keyspaceName });
		Queries.keyspaceName = keyspaceName;

		// Start a new campaign, named Gatecrasher
		Queries.askQuestions(new String[] { String.valueOf(Queries.START_NEW_CAMPAIGN), "Gatecrasher", "0" });

		final String gatecrasher = "1";

		// Fetch available research projects in Gatecrasher
		Queries.askQuestions(new String[] { String.valueOf(Queries.GET_AVAILABLE_RESEARCH), gatecrasher, "0" });
		assertEquals(1, Queries.answers.size());
		List<ResearchTask> researchTasks = (List<ResearchTask>) Queries.answers.get(0).getEntity();
		// Check that we have exactly 5 research projects available
		assertEquals(5, researchTasks.size());
		ResearchTask alienBiotech = null;
		int alienBiotechKey = 0;
		for (int key = 1; key <= researchTasks.size(); key++) {
			ResearchTask rt = researchTasks.get(key - 1);
			if (rt.name.equals("Alien Biotech")) {
				alienBiotech = rt;
				alienBiotechKey = key;
			}
		}
		// Check that Alien Biotech is available to research
		assertNotNull(alienBiotech);
		assertNotEquals(0, alienBiotechKey);

		// View Gatecrasher's inventory
		Queries.askQuestions(new String[] { String.valueOf(Queries.VIEW_INVENTORY), gatecrasher, "0" });
		assertEquals(2, Queries.answers.size());
		List<InventoryItem> inventory = (List<InventoryItem>) Queries.answers.get(1).getEntity();
		// Check that the inventory is empty
		assertEquals(0, inventory.size());

		// Research Alien Biotech
		Queries.askQuestions(new String[] { String.valueOf(Queries.ADVANCE_RESEARCH), gatecrasher, String.valueOf(alienBiotechKey), "0" });

		// Fetch the research projects that are now available
		Queries.askQuestions(new String[] { String.valueOf(Queries.GET_AVAILABLE_RESEARCH), gatecrasher, "0" });
		assertEquals(3, Queries.answers.size());
		researchTasks = (List<ResearchTask>) Queries.answers.get(2).getEntity();
		// Check that, after researching Alien Biotech, we have only 4 research projects available
		assertEquals(4, researchTasks.size());

		// Fetch all items
		Queries.askQuestions(new String[] { String.valueOf(Queries.LIST_ALL_ITEMS), "0" });
		assertEquals(4, Queries.answers.size());
		List<String> items = (List<String>) Queries.answers.get(3).getEntity();
		int sectoidCorpseKey = -1;
		for (int key = 1; key <= items.size(); key++) {
			String item = items.get(key - 1);
			if (item.equals("Sectoid Corpse")) {
				sectoidCorpseKey = key;
			}
		}
		assertNotEquals(-1, sectoidCorpseKey);

		// Acquire one Sectoid Corpse
		Queries.askQuestions(new String[] { String.valueOf(Queries.ACQUIRE_ITEM), gatecrasher, "Sectoid Corpse", "1", "0" });

		// Fetch the research projects that are now available
		Queries.askQuestions(new String[] { String.valueOf(Queries.GET_AVAILABLE_RESEARCH), gatecrasher, "0" });
		assertEquals(5, Queries.answers.size());
		researchTasks = (List<ResearchTask>) Queries.answers.get(4).getEntity();
		// Check that, after acquiring a Sectoid Corpse, a new research project has been unlocked
		assertEquals(5, researchTasks.size());
		ResearchTask sectoidAutopsy = null;
		for (int key = 1; key <= researchTasks.size(); key++) {
			ResearchTask rt = researchTasks.get(key - 1);
			if (rt.name.equals("Sectoid Autopsy")) {
				sectoidAutopsy = rt;
			}
		}
		// Check that the newly unlocked research project is Sectoid Autopsy
		assertNotNull(sectoidAutopsy);

		// Fetch all techs
		Queries.askQuestions(new String[] { String.valueOf(Queries.LIST_ALL_TECHS), "0" });
		assertEquals(6, Queries.answers.size());
		List<String> techs = (List<String>) Queries.answers.get(5).getEntity();
		int gaussWeaponsKey = -1;
		for (int key = 1; key <= techs.size(); key++) {
			String tech = techs.get(key - 1);
			if (tech.equals("Gauss Weapons")) {
				gaussWeaponsKey = key;
			}
		}
		// Check that Gauss Weapons is a tech
		assertNotEquals(-1, gaussWeaponsKey);

		// Fetch the tree of prerequisite techs that must be researched in order to unlock Gauss Weapons
		Queries.askQuestions(new String[] { String.valueOf(Queries.COMPUTE_TECH_REQUIREMENTS), String.valueOf(gaussWeaponsKey), "0" });
		assertEquals(7, Queries.answers.size());
		Set<String> techRequirements = (Set<String>) Queries.answers.get(6).getEntity();
		// Check that the prerequisites are [Modular Weapons, Magnetic Weapons]
		assertEquals(2, techRequirements.size());
		assertTrue(techRequirements.contains("Modular Weapons"));
		assertTrue(techRequirements.contains("Magnetic Weapons"));
	}

	public void assertMigrationResults() {
		final GraknClient.Transaction transaction = session.transaction().read();

		final int totalTechs = transaction.execute((GraqlGet.Aggregate) parse("match $x isa research-project; get $x; count;")).get(0).number().intValue();
		assertEquals(46, totalTechs);

		final int totalItems = transaction.execute((GraqlGet.Aggregate) parse("match $x isa item; get $x; count;")).get(0).number().intValue();
		assertEquals(34, totalItems);

		final int totalResearchTechRequirements = transaction.execute((GraqlGet.Aggregate) parse("match $x isa tech-requirement-to-begin-research; get $x; count;")).get(0).number().intValue();
		assertEquals(94, totalResearchTechRequirements);

		final int totalResearchResourceCosts = transaction.execute((GraqlGet.Aggregate) parse("match $x isa resource-cost-to-begin-research; get $x; count;")).get(0).number().intValue();
		assertEquals(44, totalResearchResourceCosts);

		transaction.close();
	}
}
