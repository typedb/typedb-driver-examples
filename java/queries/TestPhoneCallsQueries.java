package grakn.example;

import grakn.example.phoneCalls.PhoneCallsQueries;

import grakn.client.GraknClient;
import graql.lang.Graql;
import graql.lang.query.GraqlDefine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.assertEquals;


public class TestPhoneCallsQueries {

    static GraknClient client = new GraknClient("localhost:48555");
    static GraknClient.Session session = client.session("phone_calls");

    @BeforeClass
    public static void loadPhoneCallsData() throws FileNotFoundException {
        GraknClient.Transaction transaction = session.transaction().write();

        try {
            byte[] encoded = Files.readAllBytes(Paths.get("java/schema.gql"));
            String query = new String(encoded, StandardCharsets.UTF_8);
            transaction.execute((GraqlDefine) Graql.parse(query));
            transaction.commit();
            System.out.println("Loaded the phone_calls schema");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty("user.dir", new File("java/migration/csv/").getAbsolutePath());
        PhoneCallsCSVMigration.main(new String[]{});
    }

    @Test
    public void testPhoneCallsQueries() {
        List<PhoneCallsQueries.QueryExample> queryExamples = PhoneCallsQueries.getTestSubjects();

        ArrayList<String> firstActualAnswer = queryExamples.get(0).executeQuery(session.transaction().read());
        Collections.sort(firstActualAnswer);
        ArrayList<String> firstExpectedAnswer = new ArrayList<>(Arrays.asList("+54 398 559 0423", "+370 351 224 5176",
                "+62 107 530 7500", "+81 308 988 7153", "+81 746 154 2598", "+63 815 962 6097", "+7 690 597 4443", "+263 498 495 0617"));
        Collections.sort(firstExpectedAnswer);
        assertEquals(firstActualAnswer, firstExpectedAnswer);

        ArrayList<String> secondActualAnswer = queryExamples.get(1).executeQuery(session.transaction().read());
        Collections.sort(secondActualAnswer);
        ArrayList<String> secondExpectedAnswer = new ArrayList<>(Arrays.asList("+86 892 682 0628", "+86 202 257 8619",
                "+351 515 605 7915", "+86 922 760 0418", "+63 808 497 1769", "+351 272 414 6570", "+48 894 777 5173",
                "+86 825 153 5518", "+27 117 258 4149", "+1 254 875 4647", "+33 614 339 0298", "+30 419 575 7546"));
        Collections.sort(secondExpectedAnswer);
        assertEquals(secondActualAnswer, secondExpectedAnswer);

        ArrayList<String> thirdActualAnswer = new ArrayList<>(queryExamples.get(2).executeQuery(session.transaction().read()));
        Collections.sort(thirdActualAnswer);
        ArrayList<String> thirdExpectedAnswer = new ArrayList<>(Arrays.asList("+86 892 682 0628", "+54 398 559 0423"));
        Collections.sort(thirdExpectedAnswer);
        assertEquals(thirdActualAnswer, thirdExpectedAnswer);

        ArrayList<String> forthActualAnswer = new ArrayList<>(queryExamples.get(3).executeQuery(session.transaction().read()));
        Collections.sort(forthActualAnswer);
        ArrayList<String> forthExpectedAnswer = new ArrayList<>(Arrays.asList("+81 308 988 7153", "+261 860 539 4754", "+62 107 530 7500"));
        Collections.sort(forthExpectedAnswer);
        assertEquals(forthActualAnswer, forthExpectedAnswer);

        ArrayList<Float> fifthActualAnswer = queryExamples.get(4).executeQuery(session.transaction().read());
        ArrayList<Float> fifthExpectedAnswer = new ArrayList<>(Arrays.asList((float) 1242.7715, (float) 1699.4309));
        assertEquals(fifthActualAnswer, fifthExpectedAnswer);
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