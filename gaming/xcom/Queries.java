/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typedb.example.gaming.xcom;

import com.vaticle.typedb.driver.TypeDB;
import com.vaticle.typedb.driver.api.TypeDBDriver;
import com.vaticle.typedb.driver.api.TypeDBOptions;
import com.vaticle.typedb.driver.api.TypeDBSession;
import com.vaticle.typedb.driver.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Queries {
    public abstract static class Question<TChoice, TResult> {
        public abstract String getDescription();
        public abstract Result<TResult> onSubmit(TChoice choice);
    }

    public abstract static class MultipleChoiceQuestion<TResult> extends Question<Integer, TResult> {
        public abstract Map<Integer, String> getChoices();
    }

    public abstract static class TextInputQuestion<TResult> extends Question<String, TResult> {
    }

    public abstract static class NumericInputQuestion<TResult> extends Question<Number, TResult> {
    }

    static String databaseName = "xcom";
    public static List<Result<?>> answers = new ArrayList<>();

    static final int START_NEW_CAMPAIGN = 1;
    static final int GET_AVAILABLE_RESEARCH = 2;
    static final int ADVANCE_RESEARCH = 3;
    static final int VIEW_INVENTORY = 4;
    static final int ACQUIRE_ITEM = 5;
    static final int LIST_ALL_TECHS = 6;
    static final int COMPUTE_TECH_REQUIREMENTS = 7;
    static final int LIST_ALL_ITEMS = 8;

    public static void main(String[] args) {
        askQuestions(args);
    }

    public static void askQuestions(String[] args) {
        Scanner scanner;
        if (args != null && args.length > 0) {
            scanner = new Scanner(new ByteArrayInputStream(String.join(System.lineSeparator(),
                    Arrays.asList(args)).getBytes(StandardCharsets.UTF_8)));
        } else {
            scanner = new Scanner(System.in);
        }

        System.out.print("Hello, Commander.");

        final Stack<Question<?, ?>> questions = new Stack<>();
        questions.push(initialQuestion);
        while (true) {
            final Question<?, ?> question = questions.peek();
            Result<?> result;
            System.out.println();
            System.out.print(question.getDescription());
            if (question instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion<?> multipleChoiceQuestion = (MultipleChoiceQuestion<?>) question;
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
                    scanner.nextLine();
                    result = multipleChoiceQuestion.onSubmit(input);
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    System.out.println("\nPlease enter a number!");
                    continue;
                }
            } else if (question instanceof TextInputQuestion) {
                TextInputQuestion<?> textInputQuestion = (TextInputQuestion<?>) question;
                String input = scanner.nextLine();
                result = textInputQuestion.onSubmit(input);
            } else if (question instanceof NumericInputQuestion) {
                NumericInputQuestion<?> numericInputQuestion = (NumericInputQuestion<?>) question;
                double input = scanner.nextDouble();
                scanner.nextLine();
                result = numericInputQuestion.onSubmit(input);
            } else {
                throw new UnsupportedOperationException("question is not a supported question type!");
            }

            if (!result.isValid()) {
                System.out.println("\nPlease enter a valid value!");
                continue;
            }
            if (result.getQuestion() != null) {
                questions.push(result.getQuestion());
                continue;
            }
            if (result.getEntity() != null) {
                answers.add(result);
            }
            questions.clear();
            questions.push(initialQuestion);
        }
    }

    static Question<Integer, Object> initialQuestion = new MultipleChoiceQuestion<Object>() {
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
            choices.put(LIST_ALL_TECHS, "List all techs");
            choices.put(COMPUTE_TECH_REQUIREMENTS, "Compute requirements for a tech");
            choices.put(LIST_ALL_ITEMS, "List all items");
            return choices;
        }

        @Override
        public Result<Object> onSubmit(Integer option) {
            List<String> campaignNames = new ArrayList<>();
            List<String> techs = new ArrayList<>();
            List<String> items = new ArrayList<>();

            switch (option) {
                case GET_AVAILABLE_RESEARCH:
                case ADVANCE_RESEARCH:
                case VIEW_INVENTORY:
                case ACQUIRE_ITEM:
                    transaction(t -> {
                        String query = "match $campaign isa campaign, has name $name; get $name; sort $name asc;";
                        System.out.println("Executing TypeQL Query: " + query);
                        t.query().get(TypeQL.parseQuery(query).asGet()).forEach(result -> campaignNames.add(
                                result.get("name").asAttribute().getValue().asString()
                        ));
                    }, TransactionMode.READ);
                    break;

                case LIST_ALL_TECHS:
                case COMPUTE_TECH_REQUIREMENTS:
                    transaction(t -> {
                        String query = "match $tech isa research-project, has name $name; get $name; sort $name asc;";
                        System.out.println("Executing TypeQL Query: " + query);
                        t.query().get(TypeQL.parseQuery(query).asGet()).forEach(result -> techs.add(
                                result.get("name").asAttribute().getValue().asString()
                        ));
                    }, TransactionMode.READ);
                    break;

                case LIST_ALL_ITEMS:
                    transaction(t -> {
                        String query = "match $item isa item, has name $name; get $name; sort $name asc;";
                        System.out.println("Executing TypeQL Query: " + query);
                        t.query().get(TypeQL.parseQuery(query).asGet()).forEach(result -> items.add(
                                result.get("name").asAttribute().getValue().asString()
                        ));
                    }, TransactionMode.READ);
                    break;
            }

            switch (option) {
                case START_NEW_CAMPAIGN:
                    return Result.nextQuestion(chooseCampaignName);

                case GET_AVAILABLE_RESEARCH:
                    return Result.nextQuestion(chooseCampaignToGetResearchFor(campaignNames));

                case ADVANCE_RESEARCH:
                    return Result.nextQuestion(chooseCampaignToAdvanceResearchIn(campaignNames));

                case VIEW_INVENTORY:
                    return Result.nextQuestion(chooseCampaignToViewInventoryFor(campaignNames));

                case ACQUIRE_ITEM:
                    return Result.nextQuestion(chooseCampaignToAcquireItemIn(campaignNames));

                case LIST_ALL_TECHS:
                    System.out.println("\n");
                    for (int key = 1; key <= techs.size(); key++) {
                        System.out.println(key + ": " + techs.get(key - 1));
                    }
                    return new Result<>(techs);

                case COMPUTE_TECH_REQUIREMENTS:
                    return Result.nextQuestion(chooseTechToComputeRequirementsFor(techs));

                case LIST_ALL_ITEMS:
                    System.out.println("\n");
                    for (int key = 1; key <= items.size(); key++) {
                        System.out.println(key + ": " + items.get(key - 1));
                    }
                    return new Result<>(items);

                default:
                    return Result.invalid();
            }
        }
    };

    static TextInputQuestion<Object> chooseCampaignName = new TextInputQuestion<Object>() {
        @Override
        public String getDescription() {
            return "Enter your new campaign name: ";
        }

        @Override
        public Result<Object> onSubmit(String campaignName) {
            transaction(t -> {
                String query = "insert $campaign isa campaign, has name \"" + campaignName + "\";";
                System.out.println("Executing TypeQL Query: " + query);
                t.query().insert(TypeQL.parseQuery(query).asInsert());

                query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
                        + " $research_project isa research-project;"
                        + " insert (campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task, has started false;";
                System.out.println("Executing TypeQL Query: " + query);
                t.query().insert(TypeQL.parseQuery(query).asInsert());

                query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
                        + " $item isa item;"
                        + " insert (item-owner: $campaign, owned-item: $item) isa item-ownership, has quantity 0;";
                System.out.println("Executing TypeQL Query: " + query);
                t.query().insert(TypeQL.parseQuery(query).asInsert());
            }, TransactionMode.WRITE);

            System.out.println("Success");
            return new Result<>();
        }
    };

    static <TResult> MultipleChoiceQuestion<TResult> chooseFromList(List<String> names, String question, Function<Integer, Result<TResult>> onSubmit) {
        return new MultipleChoiceQuestion<TResult>() {
            @Override
            public Map<Integer, String> getChoices() {
                return IntStream.range(0, names.size())
                        .boxed()
                        .collect(Collectors.toMap(i -> i + 1, names::get));
            }

            @Override
            public String getDescription() {
                return question;
            }

            @Override
            public Result<TResult> onSubmit(Integer selectedValue) {
                return onSubmit.apply(selectedValue);
            }
        };
    }

    static Question<Integer, List<ResearchTask>> chooseCampaignToGetResearchFor(List<String> names) {
        return chooseFromList(names, "Choose a campaign", selectedKey -> {
            String campaignName = names.get(selectedKey - 1);
            List<ResearchTask> researchTasks = listAvailableResearchTasks(campaignName);
            for (ResearchTask task : researchTasks) {
                System.out.println(task.name + " [" + task.progressPercent + "% complete]");
            }
            return new Result<>(researchTasks);
        });
    }

    static MultipleChoiceQuestion<Object> chooseCampaignToAdvanceResearchIn(List<String> names) {
        return chooseFromList(names, "Choose a campaign", selectedKey -> {
            String campaignName = names.get(selectedKey - 1);
            List<ResearchTask> researchTasks = listAvailableResearchTasks(campaignName);
            return Result.nextQuestion(chooseTechToResearch(campaignName, researchTasks));
        });
    }

    static MultipleChoiceQuestion<Object> chooseTechToResearch(String campaignName, List<ResearchTask> researchTasks) {
        return chooseFromList(researchTasks
                .stream()
                .map(rt -> rt.name)
                .collect(Collectors.toList()),"Choose a technology to research", selectedKey -> {
            ResearchTask tech = researchTasks.get(selectedKey - 1);
            transaction(t -> {
                String query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
                        + " $research_project isa research-project, has name \"" + tech.name + "\";"
                        + " $task (campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task,"
                        + " has started $started; delete $task has $started;";
                System.out.println("Executing TypeQL Query: " + query);
                t.query().delete(TypeQL.parseQuery(query).asDelete());

                query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
                        + " $research_project isa research-project, has name \"" + tech.name + "\";"
                        + " $task (campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task,"
                        + " has progress $progress; delete $task has $progress;";
                System.out.println("Executing TypeQL Query: " + query);
                t.query().delete(TypeQL.parseQuery(query).asDelete());

                query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
                        + " $research_project isa research-project, has name \"" + tech.name + "\";"
                        + " $task(campaign-with-tasks: $campaign, research-task: $research_project) isa campaign-research-task;"
                        + " insert $task has started true, has progress 100;";
                System.out.println("Executing TypeQL Query: " + query);
                t.query().insert(TypeQL.parseQuery(query).asInsert());
            }, TransactionMode.WRITE);

            System.out.println("Success");
            return new Result<>();
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
            System.out.println("Executing TypeQL Query: " + query);
            t.query().get(TypeQL.parseQuery(query).asGet    ()).forEach(result -> researchTasks.add(new ResearchTask(
                    result.get("research_project_name").asAttribute().getValue().asString(),
                    result.get("progress").asAttribute().getValue().asDouble()
            )));
        }, TransactionMode.READ);

        researchTasks.sort(Comparator.comparing(rt -> rt.name));
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
            System.out.println("Executing TypeQL Query: " + query);
            t.query().get(TypeQL.parseQuery(query).asGet()).forEach(result -> inventory.add(new InventoryItem(
                    result.get("item_name").asAttribute().getValue().asString(),
                    result.get("quantity").asAttribute().getValue().asLong()
            )));
        }, TransactionMode.READ);

        inventory.sort(Comparator.comparing(item -> item.name));
        return inventory;
    }

    static MultipleChoiceQuestion<List<InventoryItem>> chooseCampaignToViewInventoryFor(List<String> names) {
        return chooseFromList(names, "Choose a campaign", selectedKey -> {
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
            return new Result<>(inventory);
        });
    }

    static MultipleChoiceQuestion<Object> chooseCampaignToAcquireItemIn(List<String> names) {
        return chooseFromList(names, "Choose a campaign", selectedKey -> {
            String campaignName = names.get(selectedKey - 1);
            List<InventoryItem> inventory = listInventory(campaignName);
            return Result.nextQuestion(chooseItemToAcquire(campaignName, inventory));
        });
    }

    static TextInputQuestion<Object> chooseItemToAcquire(String campaignName, List<InventoryItem> inventory) {
        return new TextInputQuestion<Object>() {
            @Override
            public String getDescription() {
                final StringBuilder sb = new StringBuilder();
                sb.append("Choose an item to acquire (type item name or key)\n");
                for (int key = 1; key <= inventory.size(); key++) {
                    InventoryItem i = inventory.get(key - 1);
                    sb.append(key + ": " + i.name + " [" + i.quantity + " owned]\n");
                }
                return sb.toString();
            }

            @Override
            public Result<Object> onSubmit(String s) {
                InventoryItem item;
                try {
                    int selectedKey = Integer.parseInt(s);
                    item = inventory.get(selectedKey - 1);
                } catch (NumberFormatException ex) {
                    item = inventory.stream()
                            .filter(i -> i.name.equals(s))
                            .findAny()
                            .orElse(null);
                }
                if (item == null) {
                    return Result.invalid();
                }
                return Result.nextQuestion(chooseNewItemQuantity(campaignName, item));
            }
        };
    }

    static NumericInputQuestion<Object> chooseNewItemQuantity(String campaignName, InventoryItem item) {
        return new NumericInputQuestion<Object>() {
            @Override
            public String getDescription() {
                return "How many " + item.name + " would you like to have? (you currently have " + item.quantity + ")";
            }

            @Override
            public Result<Object> onSubmit(Number input) {
                transaction(t -> {
                    String query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
                            + " $item isa item, has name \"" + item.name + "\";"
                            + " $ownership (item-owner: $campaign, owned-item: $item) isa item-ownership, has quantity $qty;"
                            + " delete $ownership has $qty;";
                    System.out.println("Executing TypeQL Query: " + query);
                    t.query().delete(TypeQL.parseQuery(query).asDelete());

                    query = "match $campaign isa campaign, has name \"" + campaignName + "\";"
                            + " $item isa item, has name \"" + item.name + "\";"
                            + " $ownership(item-owner: $campaign, owned-item: $item) isa item-ownership;"
                            + " insert $ownership has quantity " + input.longValue() + ";";
                    System.out.println("Executing TypeQL Query: " + query);
                    t.query().insert(TypeQL.parseQuery(query).asInsert());
                }, TransactionMode.WRITE);

                System.out.println("Success");
                return new Result<>();
            }
        };
    }

    static MultipleChoiceQuestion<Set<String>> chooseTechToComputeRequirementsFor(List<String> techs) {
        return chooseFromList(techs,
                "Which technology would you like to compute the required techs for?", selectedKey -> {
            String tech = techs.get(selectedKey - 1);
            Set<String> requiredTechs = new HashSet<>();
            transaction(t -> {
                String query = "match"
                        + " $tech isa research-project, has name \"" + tech + "\";"
                        + " $required-tech isa research-project, has name $required_tech_name;"
                        + " (research-to-begin: $tech, required-tech: $required-tech) isa tech-requirement-to-begin-research;"
                        + " get $required_tech_name;";
                System.out.println("Executing TypeQL Query: " + query);
                t.query().get(TypeQL.parseQuery(query).asGet()).forEach(result -> requiredTechs.add(
                        result.get("required_tech_name").asAttribute().getValue().asString()
                ));
            }, TransactionMode.READ);
            System.out.println(tech + " requires [" + String.join(", ", requiredTechs) + "]");

            return new Result<>(requiredTechs);
        });
    }

    static void transaction(Consumer<TypeDBTransaction> queries, final TransactionMode mode) {
        TypeDBDriver driver = TypeDB.coreDriver("localhost:1729");
        TypeDBSession session = driver.session(databaseName, TypeDBSession.Type.DATA);
        TypeDBOptions options = new TypeDBOptions().infer(true);
        TypeDBTransaction transaction = mode == TransactionMode.WRITE
                ? session.transaction(TypeDBTransaction.Type.WRITE, options)
                : session.transaction(TypeDBTransaction.Type.READ, options);

        queries.accept(transaction);
        if (mode == TransactionMode.WRITE) {
            transaction.commit();
        }

        transaction.close();
        session.close();
        driver.close();
    }
}
