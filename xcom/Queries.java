package grakn.example.xcom;

import grakn.client.GraknClient;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static graql.lang.Graql.parse;


public class Queries {
	public abstract static class Question<TChoice> {
		public abstract String getDescription();
		public abstract Result onSubmit(TChoice choice);
	}

	public abstract static class MultipleChoiceQuestion extends Question<Integer> {
		public abstract Map<Integer, String> getChoices();
	}

	public abstract static class TextInputQuestion extends Question<String> {
	}

	public abstract static class NumericInputQuestion extends Question<Number> {
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
            } else if (question instanceof TextInputQuestion) {
            	TextInputQuestion textInputQuestion = (TextInputQuestion) question;
                String input = scanner.next();
                result = textInputQuestion.onSubmit(input);
            } else if (question instanceof NumericInputQuestion) {
            	NumericInputQuestion numericInputQuestion = (NumericInputQuestion) question;
            	double input = scanner.nextDouble();
            	result = numericInputQuestion.onSubmit(input);
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
		private final int GET_AVAILABLE_RESEARCH = 2;
		private final int ADVANCE_RESEARCH = 3;
		private final int VIEW_INVENTORY = 4;
		private final int ACQUIRE_ITEM = 5;

		@Override
		public String getDescription() {
			return "What would you like to do?";
		}

		@Override
		public SortedMap<Integer, String> getChoices() {
			final SortedMap<Integer, String> choices = new TreeMap<>();
			choices.put(START_NEW_CAMPAIGN, "Start a new campaign");
			choices.put(GET_AVAILABLE_RESEARCH, "Get available research");
			choices.put(ADVANCE_RESEARCH, "Research a technology");
			choices.put(VIEW_INVENTORY, "View inventory");
			choices.put(ACQUIRE_ITEM, "Acquire an item");
			return choices;
		}

		@Override
		public Result onSubmit(Integer option) {
			List<String> campaignNames = new ArrayList<>();

			switch (option) {
				case GET_AVAILABLE_RESEARCH:
				case ADVANCE_RESEARCH:
				case VIEW_INVENTORY:
				case ACQUIRE_ITEM:
					transaction(t -> {
						String query = "match $campaign isa campaign, has name $name; get $name;";
						System.out.println("Executing Graql Query: " + query);
						t.execute((GraqlGet) parse(query)).forEach(result -> {
							campaignNames.add(
									result.get("name").asAttribute().value().toString()
							);
						});
					}, TransactionMode.READ);
			}

			switch (option) {
				case START_NEW_CAMPAIGN:
					return Result.ok(chooseCampaignName);

				case GET_AVAILABLE_RESEARCH:
					return Result.ok(chooseCampaignToGetResearchFor(campaignNames));

				case ADVANCE_RESEARCH:
					return Result.ok(chooseCampaignToAdvanceResearchIn(campaignNames));

				case VIEW_INVENTORY:
					return Result.ok(chooseCampaignToViewInventoryFor(campaignNames));

				case ACQUIRE_ITEM:
					return Result.ok(chooseCampaignToAcquireItemIn(campaignNames));

				default:
					return Result.invalid();
			}
		}
	};

	static Question chooseCampaignName = new TextInputQuestion() {
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
						+ " insert (campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task, has started false;";
				System.out.println("Executing Graql Query: " + query);
				t.execute((GraqlInsert) parse(query));

				query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
						+ " $item isa item;"
						+ " insert (item-owner: $campaign, owned-item: $item) isa item-ownership, has quantity 0;";
				System.out.println("Executing Graql Query: " + query);
				t.execute((GraqlInsert) parse(query));
			}, TransactionMode.WRITE);

			System.out.println("Success");
			return Result.ok();
		}
	};

	static Question chooseFromList(List<String> names, String entityType, Function<Integer, Result> onSubmit) {
		return new MultipleChoiceQuestion() {
			@Override
			public Map<Integer, String> getChoices() {
				return IntStream.range(0, names.size())
						.boxed()
						.collect(Collectors.toMap(i -> i + 1, names::get));
			}

			@Override
			public String getDescription() {
				return "Choose a " + entityType + ":";
			}

			@Override
			public Result onSubmit(Integer selectedValue) {
				return onSubmit.apply(selectedValue);
			}
		};
	}

	static Question chooseCampaignToGetResearchFor(List<String> names) {
		return chooseFromList(names, "campaign", selectedKey -> {
			String campaignName = names.get(selectedKey - 1);
			List<ResearchTask> researchTasks = listAvailableResearchTasks(campaignName);
			for (ResearchTask task : researchTasks) {
				System.out.println(task.name + " [" + task.progressPercent + "% complete]");
			}
			return Result.ok();
		});
	}

	static Question chooseCampaignToAdvanceResearchIn(List<String> names) {
		return chooseFromList(names, "campaign", selectedKey -> {
			String campaignName = names.get(selectedKey - 1);
			List<ResearchTask> researchTasks = listAvailableResearchTasks(campaignName);
			return Result.ok(chooseTechToResearch(campaignName, researchTasks));
		});
	}

	static Question chooseTechToResearch(String campaignName, List<ResearchTask> researchTasks) {
		return chooseFromList(researchTasks
				.stream()
				.map(rt -> rt.name)
				.collect(Collectors.toList()),"technology", selectedKey -> {
			ResearchTask tech = researchTasks.get(selectedKey - 1);
			transaction(t -> {
				String query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
						+ " $research_project isa research-project, has name \"" + tech.name + "\";"
						+ " (campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task,"
						+ " has started $started via $s, has progress $progress via $p;"
						+ " delete $s, $p;";
				System.out.println("Executing Graql Query: " + query);
				t.execute((GraqlDelete) parse(query));

				query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
						+ " $research_project isa research-project, has name \"" + tech.name + "\";"
						+ " $task(campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task;"
						+ " insert $task has started true, has progress 100;";
				System.out.println("Executing Graql Query: " + query);
				t.execute((GraqlInsert) parse(query));
			}, TransactionMode.WRITE);

			System.out.println("Success");
			return Result.ok();
		});
	}

	static List<ResearchTask> listAvailableResearchTasks(final String campaignName) {
		List<ResearchTask> researchTasks = new ArrayList<>();
		transaction(t -> {
			String query = "match"
					+ " $campaign isa campaign, has name \"" + campaignName + "\";"
					+ " $research-project isa research-project, has name $research_project_name;"
					+ " (campaign-with-tasks: $campaign, research-task: $research-project) isa campaign-research-task, has can-begin true, has progress $progress;"
					+ " get $research_project_name, $progress;";
			System.out.println("Executing Graql Query: " + query);
			t.execute((GraqlGet) parse(query)).forEach(result -> {
				researchTasks.add(new ResearchTask(
						result.get("research_project_name").asAttribute().value().toString(),
						(double) result.get("progress").asAttribute().value()
				));
			});
		}, TransactionMode.READ);

		return researchTasks;
	}

	static List<InventoryItem> listInventory(final String campaignName) {
		List<InventoryItem> inventory = new ArrayList<>();
		transaction(t -> {
			String query = "match"
					+ " $campaign isa campaign, has name \"" + campaignName + "\";"
					+ " $item isa item, has name $item_name;"
					+ " (item-owner: $campaign, owned-item: $item) isa item-ownership, has quantity $quantity;"
					+ " get $item_name, $quantity;";
			System.out.println("Executing Graql Query: " + query);
			t.execute((GraqlGet) parse(query)).forEach(result -> {
				inventory.add(new InventoryItem(
						result.get("item_name").asAttribute().value().toString(),
						((Long) result.get("quantity").asAttribute().value()).intValue()
				));
			});
		}, TransactionMode.READ);

		return inventory;
	}

	static Question chooseCampaignToViewInventoryFor(List<String> names) {
		return chooseFromList(names, "campaign", selectedKey -> {
			String campaignName = names.get(selectedKey - 1);
			List<InventoryItem> inventory = listInventory(campaignName)
					.stream()
					.filter(i -> i.quantity > 0)
					.collect(Collectors.toList());
			if (inventory.isEmpty()) {
				System.out.println(campaignName + "'s inventory is empty.");
			} else {
				for (InventoryItem i : inventory) {
					System.out.println(i.name + " x" + i.quantity);
				}
			}
			return Result.ok();
		});
	}

	static Question chooseCampaignToAcquireItemIn(List<String> names) {
		return chooseFromList(names, "campaign", selectedKey -> {
			String campaignName = names.get(selectedKey - 1);
			List<InventoryItem> inventory = listInventory(campaignName);
			return Result.ok(chooseItemToAcquire(campaignName, inventory));
		});
	}

	static Question chooseItemToAcquire(String campaignName, List<InventoryItem> inventory) {
		return chooseFromList(inventory
				.stream()
				.map(i -> i.name + " [" + i.quantity + " owned]")
				.collect(Collectors.toList()),"item", selectedKey -> {
			InventoryItem item = inventory.get(selectedKey - 1);
			return Result.ok(chooseNewItemQuantity(campaignName, item));
		});
	}

	static Question chooseNewItemQuantity(String campaignName, InventoryItem item) {
		return new NumericInputQuestion() {
			@Override
			public String getDescription() {
				return "How many " + item.name + " would you like to have? (you currently have " + item.quantity + ")";
			}

			@Override
			public Result onSubmit(Number input) {
				transaction(t -> {
					String query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
							+ " $item isa item, has name \"" + item.name + "\";"
							+ " (item-owner: $campaign, owned-item: $item) isa item-ownership,"
							+ " has quantity $qty via $q;"
							+ " delete $q;";
					System.out.println("Executing Graql Query: " + query);
					t.execute((GraqlDelete) parse(query));

					query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
							+ " $item isa item, has name \"" + item.name + "\";"
							+ " $ownership(item-owner: $campaign, owned-item: $item) isa item-ownership;"
							+ " insert $ownership has quantity " + input.longValue() + ";";
					System.out.println("Executing Graql Query: " + query);
					t.execute((GraqlInsert) parse(query));
				}, TransactionMode.WRITE);

				System.out.println("Success");
				return Result.ok();
			}
		};
	}

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
