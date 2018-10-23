package ai.grakn.examples;

import ai.grakn.GraknTxType;
import ai.grakn.Keyspace;
import ai.grakn.client.Grakn;
import ai.grakn.util.SimpleURI;

import java.util.*;


public class Queries {
    abstract static class QueryExample {
        String question;

        public QueryExample(String question) {
            this.question = question;
        }

        String getQuestion() { return this.question; }

        abstract void executeQuery(Grakn.Transaction transaction);
    }

    public static void main(String[] args) {
        List<QueryExample> queryExamples = initialiseQueryExamples();

        Scanner scanner = new Scanner(System.in);

        System.out.print("For which of these questions, on the phone_calls knowledge graph, do you want to execute the query?\n");
        for (int i = 0; i < queryExamples.size(); i++) {
            System.out.println(i + 1 + ": " + queryExamples.get(i).getQuestion());
        }

        int qsNumber = -1;
        while (qsNumber < 0 || qsNumber > queryExamples.size()) {
            System.out.print("choose a number (enter 0 for to answer all questions): ");
            try {
                qsNumber = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println();
                System.out.println("Please enter a number!");
            }

        }
        System.out.println();

        SimpleURI localGrakn = new SimpleURI("localhost", 48555);
        Keyspace keyspace = Keyspace.of("phone_calls");
        Grakn grakn = new Grakn(localGrakn);
        Grakn.Session session = grakn.session(keyspace);
        Grakn.Transaction transaction = session.transaction(GraknTxType.WRITE);

        if (qsNumber == 0) {
            queryExamples.forEach(queryExample -> {
                queryExample.executeQuery(transaction);
            });
        } else {
            queryExamples.get(qsNumber - 1).executeQuery(transaction);
        }

        transaction.close();
        session.close();
    }

    // GRAQL QUERY EXAMPLES
    static List<QueryExample> initialiseQueryExamples() {
        List<QueryExample> queryExamples = new ArrayList<>();

        queryExamples.add(new QueryExample("Since September 14th, which customers called the person with phone number +86 921 547 9004?") {
            @Override
            void executeQuery(Grakn.Transaction tx) {
                printToLog("Question: ", this.question);

                List<String> queryAsList = Arrays.asList(
                        "match",
                        "  $customer isa person has phone-number $phone-number;",
                        "  $company isa company has name \"Telecom\";",
                        "  (customer: $customer, provider: $company) isa contract;",
                        "  $target isa person has phone-number \"+86 921 547 9004\";",
                        "  (caller: $customer, callee: $target) isa call has started-at $started-at;",
                        "  $min-date == 2018-09-14T17:18:49; $started-at > $min-date;",
                        "get $phone-number;"
                );

                printToLog("Query:", String.join("\n", queryAsList));
                String query = String.join("", queryAsList);

                List<String> result = new ArrayList<>();
                tx.graql().parse(query).execute().forEach(answer -> {
                    result.add(answer.asConceptMap().get("phone-number").asAttribute().value().toString());
                });
                printToLog("Result: ", String.join(", ", result));
            }
        });

        queryExamples.add(new QueryExample("Who are the people who have received a call from a London customer aged over 50 who has previously called someone aged under 20?") {
            @Override
            void executeQuery(Grakn.Transaction tx) {
                printToLog("Question: ", this.question);

                List<String> queryAsList = Arrays.asList(
                        "match ",
                        "  $suspect isa person has city \"London\", has age > 50;",
                        "  $company isa company has name \"Telecom\";",
                        "  (customer: $suspect, provider: $company) isa contract;",
                        "  $pattern-callee isa person has age < 20;",
                        "  (caller: $suspect, callee: $pattern-callee) isa call has started-at $pattern-call-date;",
                        "  $target isa person has phone-number $phone-number, has is-customer false;",
                        "  (caller: $suspect, callee: $target) isa call has started-at $target-call-date;",
                        "  $target-call-date > $pattern-call-date;",
                        "get $phone-number;"
                );

                printToLog("Query:", String.join("\n", queryAsList));
                String query = String.join("", queryAsList);

                List<String> result = new ArrayList<>();
                tx.graql().parse(query).execute().forEach(answer -> {
                    result.add(answer.asConceptMap().get("phone-number").asAttribute().value().toString());
                });
                printToLog("Result: ", String.join(", ", result));
            }
        });

        queryExamples.add(new QueryExample("Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176?") {
            @Override
            void executeQuery(Grakn.Transaction tx) {
                printToLog("Question: ", this.question);

                List<String> queryAsList = Arrays.asList(
                        "match ",
                        "  $common-contact isa person has phone-number $phone-number;",
                        "  $customer-a isa person has phone-number \"+7 171 898 0853\";",
                        "  $customer-b isa person has phone-number \"+370 351 224 5176\";",
                        "  (caller: $customer-a, callee: $common-contact) isa call;",
                        "  (caller: $customer-b, callee: $common-contact) isa call;",
                        "get $phone-number;"
                );

                printToLog("Query:", String.join("\n", queryAsList));
                String query = String.join("", queryAsList);

                List<String> result = new ArrayList<>();
                tx.graql().parse(query).execute().forEach(answer -> {
                    result.add(answer.asConceptMap().get("phone-number").asAttribute().value().toString());
                });
                printToLog("Result: ", String.join(", ", result));
            }
        });

        queryExamples.add(new QueryExample("Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?") {
            @Override
            void executeQuery(Grakn.Transaction tx) {
                printToLog("Question: ", this.question);

                List<String> queryAsList = Arrays.asList(
                        "match ",
                        "  $target isa person has phone-number \"+48 894 777 5173\";",
                        "  $company isa company has name \"Telecom\";",
                        "  $customer-a isa person has phone-number $phone-number-a;",
                        "  (customer: $customer-a, provider: $company) isa contract;",
                        "  (caller: $customer-a, callee: $target) isa call;",
                        "  $customer-b isa person has phone-number $phone-number-b;",
                        "  (customer: $customer-b, provider: $company) isa contract;",
                        "  (caller: $customer-b, callee: $target) isa call;",
                        "  (caller: $customer-a, callee: $customer-b) isa call;",
                        "get $phone-number-a, $phone-number-b;"
                );

                printToLog("Query:", String.join("\n", queryAsList));
                String query = String.join("", queryAsList);

                Set<String> result = new HashSet<>();
                tx.graql().parse(query).execute().forEach(answer -> {
                    System.out.println(answer);
                    result.add(answer.asConceptMap().get("phone-number-a").asAttribute().value().toString());
                    result.add(answer.asConceptMap().get("phone-number-b").asAttribute().value().toString());
                });
                printToLog("Result: ", String.join(", ", result));
            }
        });

        queryExamples.add(new QueryExample("How does the average call duration among customers aged under 20 compare those aged over 40?") {
            @Override
            void executeQuery(Grakn.Transaction tx) {
                printToLog("Question: ", this.question);

                List<String> firstQueryAsList = Arrays.asList(
                        "match",
                        "  $customer isa person has age < 20;",
                        "  $company isa company has name \"Telecom\";",
                        "  (customer: $customer, provider: $company) isa contract;",
                        "  (caller: $customer, callee: $anyone) isa call has duration $duration;",
                        "aggregate mean $duration;"
                );

                printToLog("First Query:", String.join("\n", firstQueryAsList));
                String firstQuery = String.join("", firstQueryAsList);

                float fisrtResult = tx.graql().parse(firstQuery).execute().get(0).asValue().number().floatValue();
                String result = "Customers aged under 20 have made calls with average duration of " + fisrtResult + " seconds.\n";

                List<String> secondQueryAsList = Arrays.asList(
                        "match",
                        "  $customer isa person has age > 40;",
                        "  $company isa company has name \"Telecom\";",
                        "  (customer: $customer, provider: $company) isa contract;",
                        "  (caller: $customer, callee: $anyone) isa call has duration $duration;",
                        "aggregate mean $duration;"
                );

                printToLog("Second Query:", String.join("\n", secondQueryAsList));
                String secondQuery = String.join("", secondQueryAsList);

                float secondResult = tx.graql().parse(secondQuery).execute().get(0).asValue().number().floatValue();
                result += "Customers aged over 40 have made calls with average duration of " + secondResult + " seconds.\n";

                printToLog("Result: ", String.join(", ", result));

            }
        });

        // TO ADD A NEW QUERY EXAMPLE:
//        queryExamples.add(new QueryExample("question goes here") {
//            @Override
//            void executeQuery(Grakn.Transaction tx) {
//                printToLog("Question: ", this.question);
//
//                // queries are written as a list for better readability
//                List<String> queryAsList = Arrays.asList(
//                        "each line;",
//                        "  as an element;",
//                        "ends with semicolon"
//                );
//
//                // join the query list elements with a new line before printing
//                printToLog("Query:", String.join("\n", queryAsList));
//                // join the query list elements to obtain the quer as a string to be executed
//                String query = String.join("", queryAsList);
//
//                List<String> result = new ArrayList<>();
//                tx.graql().parse(query).execute().forEach(answer -> {
//                    // retrieve answers
//                });
//                printToLog("Result: ", String.join(", ", result));
//            }
//        });

        return queryExamples;
    }

    static void printToLog(String title, String content) {
        System.out.println(title);
        System.out.println();
        System.out.println(content);
        System.out.println();
    }
}
