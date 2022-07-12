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

const { TypeDB } = require("typedb-client/TypeDB");
const { SessionType } = require("typedb-client/api/connection/TypeDBSession");
const { TransactionType } = require("typedb-client/api/connection/TypeDBTransaction");

const readline = require("readline");

// to add a new query implementation:
//   1. add the question and function to the approriate list of dictionaries:
//     current lists are: getQsFunc, aggregateQsFunc and computeQsFunc
//     example:
//       getQsFunc = [
//         ...
//         {
//           "question": "new question?"
//           "queryFunction": executeQuery#
//         }
//       ]
//   2. add the function and its implementation:
//     use the template below

// the template for executeQuery functions
// function executeQueryFormat(question) {
//   printToLog("Question: ", question)
//   // queries are written as a list for better readibility
//   const query = [
//     "each line;",
//     "as an element;",
//     "ends with simocolon;",
//   ];

//   // join the query list elements with a new line before console.loging
//   printToLog("Query:", query.join("\n"));
//   // join the query list elements to obtain the quer as a string to be executed
//   const query = query.join("");
//   const iterator = await transaction.query(query);
//   // ... retrieve the answers
//   const result = "example result";
//   printToLog("Result:", result);
// }

const getQsFunc = [
	{
		question:
			"From 2018-09-10 onwards, which customers called the person with phone number +86 921 547 9004?",
		queryFunction: executeQuery1
	},
	{
		question:
			"Who are the people aged under 20 who have received at least one phone call from a Cambridge customer aged over 50?",
		queryFunction: executeQuery2
	},
	{
		question:
			"Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176?",
		queryFunction: executeQuery3
	},
	{
		question:
			"Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?",
		queryFunction: executeQuery4
	}
];

const aggregateQsFunc = [
	{
		question:
			"How does the average call duration among customers aged under 20 compare those aged over 40?",
		queryFunction: executeQuery5
	}
];

const queryExamples = getQsFunc.concat(aggregateQsFunc);

// utils
const log = console.log;
function printToLog(title, content) {
	log(title);
	log("");
	log(content);
	log("\n");
}

// From 2018-09-10 onwards, which customers called person with phone number +86 921 547 9004?
async function executeQuery1(question, transaction) {
	printToLog("Question: ", question);

	let query = [
		"match",
		"  $customer isa person, has phone-number $phone-number;",
		'  $company isa company, has name "Telecom";',
		"  (customer: $customer, provider: $company) isa contract;",
		'  $target isa person, has phone-number "+86 921 547 9004";',
		"  (caller: $customer, callee: $target) isa call, has started-at $started-at;",
		"  $min-date 2018-09-14T17:18:49; $started-at > $min-date;",
		"get $phone-number;"
	];
	printToLog("Query:", query.join("\n"));
	query = query.join("");

	const iterator = transaction.query().match(query);
	const answers = await iterator.collect();
	const result = await Promise.all(
		answers.map(answer =>
			answer.map()
				  .get("phone-number")
				  .getValue()
		)
	);

	printToLog("Result:", result);

	return result;
}

// who are the people aged under 20 who have received at least one phone call from a Cambridge customer aged over 60?
async function executeQuery2(question, transaction) {
	printToLog("Question: ", question);

	let query = [
		"match ",
		'  $suspect isa person, has city "London", has age > 50;',
		'  $company isa company, has name "Telecom";',
		"  (customer: $suspect, provider: $company) isa contract;",
		"  $pattern-callee isa person, has age < 20;",
		"  (caller: $suspect, callee: $pattern-callee) isa call, has started-at $pattern-call-date;",
		"  $target isa person, has phone-number $phone-number;",
        "  not { (customer: $target, provider: $company) isa contract; };",
		"  (caller: $suspect, callee: $target) isa call, has started-at $target-call-date;",
		"  $target-call-date > $pattern-call-date;",
		"get $phone-number;"
	];
	printToLog("Query:", query.join("\n"));
	query = query.join("");

	const iterator = transaction.query().match(query);
	const answers = await iterator.collect();
	const result = await Promise.all(
		answers.map(answer =>
			answer.map()
				  .get("phone-number")
				  .getValue()
		)
	);

	printToLog("Result:", result);

    return result;
}

// "Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176?
async function executeQuery3(question, transaction) {
	printToLog("Question: ", question);

	let query = [
		"match ",
		"  $common-contact isa person, has phone-number $phone-number;",
		'  $customer-a isa person, has phone-number "+7 171 898 0853";',
		'  $customer-b isa person, has phone-number "+370 351 224 5176";',
		"  (caller: $customer-a, callee: $common-contact) isa call;",
		"  (caller: $customer-b, callee: $common-contact) isa call;",
		"get $phone-number;"
	];
	printToLog("Query:", query.join("\n"));
	query = query.join("");

	const iterator = transaction.query().match(query);
	const answers = await iterator.collect();
	const result = await Promise.all(
		answers.map(answer =>
			answer.map()
				  .get("phone-number")
				  .getValue()
		)
	);

	printToLog("Result:", result);

    return result;
}

//  Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?",
async function executeQuery4(question, transaction) {
	printToLog("Question: ", question);

	let query = [
		"match ",
		'  $target isa person, has phone-number "+48 894 777 5173";',
		'  $company isa company, has name "Telecom";',
		"  $customer-a isa person, has phone-number $phone-number-a;",
		"  (customer: $customer-a, provider: $company) isa contract;",
		"  (caller: $customer-a, callee: $target) isa call;",
		"  $customer-b isa person, has phone-number $phone-number-b;",
		"  (customer: $customer-b, provider: $company) isa contract;",
		"  (caller: $customer-b, callee: $target) isa call;",
		"  (caller: $customer-a, callee: $customer-b) isa call;",
		"get $phone-number-a, $phone-number-b;"
	];
	printToLog("Query:", query.join("\n"));
	query = query.join("");

	const iterator = transaction.query().match(query);
	const answers = await iterator.collect();
	const result = await Promise.all(
		answers.map(answer =>
			answer.map()
				  .get("phone-number-a")
				  .getValue()
		)
	);

	printToLog("Result:", result);

    return result;
}

// How does the average call duration among customers aged under 20 compare those aged over 40?
async function executeQuery5(question, transaction) {
	printToLog("Question: ", question);

	firstQuery = [
		'match',
		'  $customer isa person, has age < 20;',
		'  $company isa company, has name "Telecom";',
		'  (customer: $customer, provider: $company) isa contract;',
		'  (caller: $customer, callee: $anyone) isa call, has duration $duration;',
		'get $duration; mean $duration;'
	];

	printToLog("Query:", firstQuery.join("\n"));
	firstQuery = firstQuery.join("");

    result = [];
	const firstAnswer = await transaction.query().matchAggregate(firstQuery);
	let firstResult = 0;
	if(firstAnswer.isNumber()) {
		firstResult = firstAnswer.asNumber()
	}

	let output =
		"Customers aged under 20 have made calls with average duration of " +
		Math.round(firstResult) +
		" seconds.\n";

	result.push(firstResult);

	secondQuery = [
		'match ' +
		'  $customer isa person, has age > 40;',
		'  $company isa company, has name "Telecom";',
		'  (customer: $customer, provider: $company) isa contract;',
		'  (caller: $customer, callee: $anyone) isa call, has duration $duration;',
		'get $duration; mean $duration;'
	];
	printToLog("Query:", secondQuery.join("\n"));
	secondQuery = secondQuery.join("");

	const secondAnswer = await transaction.query().matchAggregate(secondQuery);
	let secondResult = 0;
	if(secondAnswer.isNumber()) {
		secondResult = secondAnswer.asNumber()
	}

	output +=
		"Customers aged over 40 have made calls with average duration of " +
		Math.round(secondResult) +
		" seconds.\n";

    result.push(secondResult);

	printToLog("Result:", output);

    return result;
}

//
async function executeQuery6(question, transaction) {
	printToLog("Question: ", question);
}

//
async function executeQuery7(question, transaction) {
	printToLog("Question: ", question);
}

// execute all queries for all questions
async function executeAllQueries(transaction) {
	for (queryExample of queryExamples) {
		question = queryExample["question"];
		queryFunction = queryExample["queryFunction"];
		await queryFunction(question, transaction);
		log("\n - - -  - - -  - - -  - - - \n");
	}
}

/**
 * this is the main function
 * prints the questions and asks for the input, based on which the corresponding function
 * to execute the query is called
 */
async function executeQueries() {
	// print questions
	log("\nSelect a question for which you'd like to execute the query?\n");
	for (let [index, queryExample] of queryExamples.entries())
		log(index + 1 + ". " + queryExample["question"]);
	log("");

	// get user's selection and call the function for it
	const rl = readline.createInterface({
		input: process.stdin,
		output: process.stdout
	});
	executeBasedOnSelection(rl);
}
/**
 * a recursive function that terminates after receiving a valid input
 * otherwise keeps asking
 * @param {object} rl the readline insterface to receive input via console
 * exists on completion
 */
function executeBasedOnSelection(rl) {
	const question = "choose a number (0 for to answer all questions): ";
	rl.question(question, async function (answer, rl) {
		if (answer >= 0 && answer < queryExamples.length + 1) {
			await processSelection(answer, "phone_calls");
			process.exit(0);
		}
		executeBasedOnSelection(rl);
	});
}
/**
 * 1. create an instance of TypeDB, connecting to the server
 * 2. create a session of the instance, connecting to the database phone_calls
 * 3. create a transaction, off the session
 * 4. call the function corresponding to the selected question
 * 5. close the transaction
 * 6. close the session
 * 7. closes the client
 * @param {integer} qsNumber the (question) number selected by the user
 */
async function processSelection(qsNumber, databaseName) {
	const client = TypeDB.coreClient("localhost:1729"); // 1
	const session = await client.session(databaseName, SessionType.DATA); // 2
	const transaction = await session.transaction(TransactionType.READ); // 3

	if (qsNumber === 0) {
		await executeAllQueries(transaction); // 4
	} else {
		const question = queryExamples[qsNumber - 1]["question"];
		const queryFunction = queryExamples[qsNumber - 1]["queryFunction"];
		await queryFunction(question, transaction); // 4
	}
	await transaction.close(); // 5
	await session.close(); // 6
	client.close(); // 7
}

module.exports.processSelection = processSelection;
module.exports.queryExamples = queryExamples;
module.exports.init = executeQueries;
