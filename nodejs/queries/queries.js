const Grakn = require("grakn");
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
//   const iterator = await tx.query(query);
//   // ... retrieve the answers
//   const result = "example result";
//   printToLog("Result:", result);
// }

const log = console.log;
function printToLog(title, content) {
  log(title);
  log("");
  log(content);
  log("\n");
}

const getQsFunc = [
  {
    question:
      "From 2018-09-10 onwards, which customers called the person with phone number +86 921 547 9004?",
    queryFunction: executeQuery1
  },
  {
    question:
      "Who are the people aged under 20 who have received at least one phone call from a Cambridge customer aged above 50?",
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
      "How does the average call duration among customers aged under 20 compare those aged above 40?",
    queryFunction: executeQuery5
  }
];

const computeQsFunc = [
  {
    question: "compute-related first question goes here ",
    queryFunction: executeQuery6
  },
  {
    question: "compute-elated second question goes here ",
    queryFunction: executeQuery7
  }
];

const questionsAndFunctions = getQsFunc
  .concat(aggregateQsFunc)
  .concat(computeQsFunc);

// From 2018-09-10 onwards, which customers called person with phone number +86 921 547 9004?
async function executeQuery1(question, tx) {
  log(tx);

  printToLog("Question: ", question);

  let query = [
    "match",
    "  $customer isa person has phone-number $phone-number;",
    '  $company isa company has name "Telecom";',
    "  (customer: $customer, provider: $company) isa contract;",
    '  $target isa person has phone-number "+86 921 547 9004";',
    "  (caller: $customer, callee: $target) isa call has started-at $started-at;",
    "  $min-date == 2018-09-14T17:18:49; $started-at > $min-date;",
    "get $phone-number;"
  ];
  printToLog("Query:", query.join("\n"));
  query = query.join("");

  const iterator = await tx.query(query);
  const answers = await iterator.collect();
  const result = await Promise.all(
    answers.map(answer =>
      answer
        .map()
        .get("phone-number")
        .value()
    )
  );

  printToLog("Result:", result);
}

// who are the people aged under 20 who have received at least one phone call from a Cambridge customer aged above 60?
async function executeQuery2(question, tx) {
  printToLog("Question: ", question);

  let query = [
    "match ",
    "  $person isa person has phone-number $phone-number has age < 20;",
    '  $customer isa person has city "London", has age > 60;',
    '  $company isa company has name "Telecom";',
    "  (customer: $customer, provider: $company) isa contract;",
    "  (caller: $customer, callee: $person) isa call;",
    "get $phone-number;"
  ];
  printToLog("Query:", query.join("\n"));
  query = query.join("");

  const iterator = await tx.query(query);
  const answers = await iterator.collect();
  const result = await Promise.all(
    answers.map(answer =>
      answer
        .map()
        .get("phone-number")
        .value()
    )
  );

  printToLog("Result:", result);
}

// "Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176?
async function executeQuery3(question, tx) {
  printToLog("Question: ", question);

  let query = [
    "match ",
    "  $common-contact isa person has phone-number $phone-number;",
    '  $customer-a isa person has phone-number "+7 171 898 0853";',
    '  $customer-b isa person has phone-number "+370 351 224 5176";',
    "  (caller: $customer-a, callee: $common-contact) isa call;",
    "  (caller: $customer-b, callee: $common-contact) isa call;",
    "get $phone-number;"
  ];
  printToLog("Query:", query.join("\n"));
  query = query.join("");

  const iterator = await tx.query(query);
  const answers = await iterator.collect();
  const result = await Promise.all(
    answers.map(answer =>
      answer
        .map()
        .get("phone-number")
        .value()
    )
  );

  printToLog("Result:", result);
}

//  Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?",
async function executeQuery4(question, tx) {
  printToLog("Question: ", question);

  let query = [
    "match ",
    '  $target isa person has phone-number "+48 894 777 5173";',
    '  $company isa company has name "Telecom";',
    "  $customer-a isa person has phone-number $phone-number-a;",
    "  $customer-b isa person has phone-number $phone-number-b;",
    "  (customer: $customer-a, provider: $company) isa contract;",
    "  (customer: $customer-b, provider: $company) isa contract;",
    "  (caller: $customer-a, callee: $customer-b) isa call;",
    "  (caller: $customer-a, callee: $target) isa call;",
    "  (caller: $customer-b, callee: $target) isa call;",
    "get $phone-number-a, $phone-number-b;"
  ];
  printToLog("Query:", query.join("\n"));
  query = query.join("");

  const iterator = await tx.query(query);
  const answers = await iterator.collect();
  const result = await Promise.all(
    answers.map(answer =>
      answer
        .map()
        .get("phone-number-a")
        .value()
    )
  );

  printToLog("Result:", result);
}

// How does the average call duration among customers aged under 20 compare those aged above 40?
async function executeQuery5(question, tx) {
  printToLog("Question: ", question);

  let queryA = [
    "match",
    "  $customer isa person has age < 20;",
    '  $company isa company has name "Telecom";',
    "  (customer: $customer, provider: $company) isa contract;",
    "  (caller: $customer, callee: $anyone) isa call has duration $duration;",
    "aggregate mean $duration;"
  ];
  printToLog("Query:", queryA.join("\n"));
  queryA = queryA.join("");

  const iteratorA = await tx.query(queryA);
  const answersA = await iteratorA.collect();
  const resultA = answersA[0].number();
  let result =
    "Customers aged under 20 have made calls with average duration of " +
    Math.round(resultA) +
    " seconds.\n";

  let queryB = [
    "match " + "  $customer isa person has age > 40;",
    '  $company isa company has name "Telecom";',
    "  (customer: $customer, provider: $company) isa contract;",
    "  (caller: $customer, callee: $anyone) isa call has duration $duration;",
    "aggregate mean $duration;"
  ];
  printToLog("Query:", queryB.join("\n"));
  queryB = queryB.join("");

  const iteratorB = await tx.query(queryB);
  const answersB = await iteratorB.collect();
  const resultB = answersB[0].number();
  result +=
    "Customers aged above 40 have made calls with average duration of " +
    Math.round(resultB) +
    " seconds.\n";

  printToLog("Result:", result);
}

//
async function executeQuery6(question, tx) {
  printToLog("Question: ", question);
}

//
async function executeQuery7(question, tx) {
  printToLog("Question: ", question);
}

async function executeAllQueries(tx) {
  for (qsFunc of questionsAndFunctions) {
    qustion = qsFunc["question"];
    queryFunction = qsFunc["queryFunction"];
    await queryFunction(qustion, tx);
    log("\n - - -  - - -  - - -  - - - \n");
  }
}

// the code below:
//   - prints the questions
//   - gets user's selection wrt the queries to be executed
//   - creates a Grakn client > session > transaction connected to the phone_calls keyspace
//   - runs the right function based on the user's selection
//   - closes the session

executeQueries();

async function executeQueries() {
  // print questions
  log("\nSelect a question for which you'd like to execute the query?\n");
  for (let [index, qsFunc] of questionsAndFunctions.entries())
    log(index + 1 + ". " + qsFunc["question"]);
  log("");

  // get user's selection and call the function for it
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
  });
  executeBasedOnSelection(rl);
}

function executeBasedOnSelection(rl) {
  const question = "choose a number (0 for to answer all questions): ";
  rl.question(question, async function(answer, rl) {
    if (answer >= 0 && answer < questionsAndFunctions.length + 1) {
      await processSelection(answer);
      process.exit(0);
      return false;
    }
    executeBasedOnSelection(rl);
  });
}

async function processSelection(qsNumber) {
  const grakn = new Grakn("localhost:48555");
  const session = await grakn.session((keyspace = "phone_calls"));
  const tx = await session.transaction(Grakn.txType.WRITE);

  if (qsNumber == 0) {
    await executeAllQueries(tx);
  } else {
    const question = questionsAndFunctions[qsNumber - 1]["question"];
    const queryFunction = questionsAndFunctions[qsNumber - 1]["queryFunction"];
    await queryFunction(question, tx);
  }

  session.close();
}
