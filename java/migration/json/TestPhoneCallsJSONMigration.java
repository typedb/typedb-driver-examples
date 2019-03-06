package grakn.example;

import grakn.client.GraknClient;
import graql.lang.Graql;
import static graql.lang.Graql.*;
import static org.junit.Assert.assertEquals;

import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlQuery;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.*;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPhoneCallsCSVMigration {

    static GraknClient client = new GraknClient("localhost:48555");
    static GraknClient.Session session = client.session("phone_calls");

    @Before
    public void loadPhoneCalls() {
        GraknClient.Transaction transaction = session.transaction().write();

        try {
            byte[] encoded = Files.readAllBytes(Paths.get("java/schema.gql"));
            String query = new String(encoded, StandardCharsets.UTF_8);
            transaction.execute((GraqlQuery) Graql.parse(query));
            transaction.commit();
            System.out.println("Loaded the phone_calls schema");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty("user.dir", new File("java/migration/csv/").getAbsolutePath());
    }

    @Test
    public void testPhoneCallsCSVMigration() throws IOException {
        PhoneCallsCSVMigration.main(new String[]{});
        assertResults();
    }

    public void assertResults() {
        GraknClient.Transaction transaction = session.transaction().read();

        Number numberOfPeople = transaction.execute((GraqlGet.Aggregate) parse("match $x isa person; get $x; count;")).get(0).number().intValue();
        assertEquals(numberOfPeople, 30);

        Number numberOfCompanies = transaction.execute((GraqlGet.Aggregate) parse("match $x isa company; get $x; count;")).get(0).number();
        assertEquals(numberOfCompanies,1);

        Number numberOfContracts = transaction.execute((GraqlGet.Aggregate) parse("match $x isa contract; get $x; count;")).get(0).number();
        assertEquals(numberOfContracts, 10);

        Number numberOfCalls = transaction.execute((GraqlGet.Aggregate) parse("match $x isa call; get $x; count;")).get(0).number();
        assertEquals(numberOfCalls,200);

        transaction.close();
    }

    @After
    public void cleanPhoneCalls() {
        client.keyspaces().delete("phone_calls");
        System.out.println("Deleted the phone_calls keyspace");
    }

    @AfterClass
    public static void close() {
        session.close();
        client.close();
    }
}