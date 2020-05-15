package grakn.example.xcom;

import grakn.client.GraknClient;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;

import java.util.*;
import java.util.function.Consumer;

import static graql.lang.Graql.parse;


public class Queries {
	public abstract static class Question<TChoice> {
		public abstract String getDescription();
		public abstract Result onSubmit(TChoice choice);
	}

	public abstract static class MultipleChoiceQuestion extends Question<Integer> {
		public abstract Map<Integer, String> getChoices();
	}

	public abstract static class FreeTextQuestion<TChoice> extends Question<TChoice> {
	}

	public static class Result {
		private Result(boolean valid, Question<?> question) {
			this.valid = valid;
			this.question = question;
		}

		public static Result ok() {
			return new Result(true, null);
		}

		public static Result ok(Question<?> question) {
			return new Result(true, question);
		}

		public static Result invalid() {
			return new Result(false, null);
		}

		public Question<?> getQuestion() {
			return question;
		}

		public boolean isValid() {
			return valid;
		}

		private final boolean valid;
		private final Question<?> question;
	}

	static String keyspaceName = "xcom";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Hello, Commander.");

		final Stack<Question<?>> questions = new Stack<>();
		questions.push(initialQuestion);
		while (true) {
			final Question<?> question = questions.peek();
			Result result = null;
			System.out.println();
			System.out.print(question.getDescription());
            if (question instanceof MultipleChoiceQuestion) {
            	MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
            	System.out.println("\n");
	            for (Map.Entry<Integer, String> option : multipleChoiceQuestion.getChoices().entrySet()) {
		            System.out.println(option.getKey() + ": " + option.getValue());
	            }
	            System.out.print("choose a number (enter 0 to exit): ");
	            try {
		            int input = scanner.nextInt();
		            if (input == 0) {
			            break;
		            }
		            result = multipleChoiceQuestion.onSubmit(input);
	            } catch (InputMismatchException e) {
		            scanner.nextLine();
		            System.out.println("\nPlease enter a number!");
		            continue;
	            }
            } else if (question instanceof FreeTextQuestion) {
            	FreeTextQuestion freeTextQuestion = (FreeTextQuestion) question;
                String input = scanner.next();
                result = freeTextQuestion.onSubmit(input);
            }

			if (!result.isValid()) {
				scanner.nextLine();
				System.out.println("\nPlease enter a valid value!");
				continue;
			}
			if (result.getQuestion() != null) {
				scanner.nextLine();
				questions.push(result.getQuestion());
				continue;
			}
			questions.clear();
			questions.push(initialQuestion);
		}
	}

	static Question initialQuestion = new MultipleChoiceQuestion() {
		private final int START_NEW_CAMPAIGN = 1;

		@Override
		public String getDescription() {
			return "What would you like to do?";
		}

		@Override
		public SortedMap<Integer, String> getChoices() {
			final SortedMap<Integer, String> choices = new TreeMap<>();
			choices.put(START_NEW_CAMPAIGN, "Start a new campaign");
			return choices;
		}

		@Override
		public Result onSubmit(Integer option) {
			switch (option) {
				case START_NEW_CAMPAIGN:
					return Result.ok(chooseCampaignName);
				default:
					return Result.invalid();
			}
		}
	};

	static Question chooseCampaignName = new FreeTextQuestion<String>() {
		@Override
		public String getDescription() {
			return "Enter your new campaign name: ";
		}

		@Override
		public Result onSubmit(String campaignName) {
			transaction(t -> {
				String query = "insert $campaign isa campaign, has name \"" + campaignName + "\";";
				System.out.println("Executing Graql Query: " + query);
				t.execute((GraqlInsert) parse(query));

				query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
						+ " $research_project isa research-project;"
						+ " insert (campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task, has progress 0;";
				System.out.println("Executing Graql Query: " + query);
				t.execute((GraqlInsert) parse(query));
			}, TransactionMode.WRITE);

			System.out.println("Success");
			return Result.ok();
		}
	};

	static void transaction(Consumer<GraknClient.Transaction> queries, final TransactionMode mode) {
		GraknClient client = new GraknClient("localhost:48555");
		GraknClient.Session session = client.session(keyspaceName);
		GraknClient.Transaction transaction = mode == TransactionMode.WRITE
				? session.transaction().write()
				: session.transaction().read();

		queries.accept(transaction);
		if (mode == TransactionMode.WRITE) {
			transaction.commit();
		}

		transaction.close();
		session.close();
		client.close();
	}

	/*// GRAQL QUERY EXAMPLES
	static List<QueryExample> initialiseQueryExamples() {
		List<QueryExample> queryExamples = new ArrayList<>();

		queryExamples.add(new QueryExample("Since September 14th, which customers called the person with phone number +86 921 547 9004?") {
			@Override
			public <T> T executeQuery(GraknClient.Transaction transaction) {
				printToLog("Question: ", this.description);

				List<String> queryAsList = Arrays.asList(
						"match",
						"  $customer isa person, has phone-number $phone-number;",
						"  $company isa company, has name \"Telecom\";",
						"  (customer: $customer, provider: $company) isa contract;",
						"  $target isa person, has phone-number \"+86 921 547 9004\";",
						"  (caller: $customer, callee: $target) isa call, has started-at $started-at;",
						"  $min-date == 2018-09-14T17:18:49; $started-at > $min-date;",
						"get $phone-number;"
				);

				printToLog("Query:", String.join("\n", queryAsList));
				String query = String.join("", queryAsList);

				List<String> result = new ArrayList<>();
				transaction.execute((GraqlGet) parse(query)).forEach(answer -> {
					result.add(
							answer.get("phone-number").asAttribute().value().toString()
					);
				});

				printToLog("Result: ", String.join(", ", result));

				return (T) result;
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
	}*/

	static void printToLog(String title, String content) {
		System.out.println(title);
		System.out.println();
		System.out.println(content);
		System.out.println();
	}
}
