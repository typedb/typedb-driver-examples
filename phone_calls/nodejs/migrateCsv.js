// the Node.js client for Grakn
// https://github.com/graknlabs/grakn/tree/master/client-nodejs
const Grakn = require("grakn-client");

// used for creating a stream to read the data files
// https://nodejs.org/api/fs.html#fs_fs_createreadstream_path_options
const fs = require("fs");

// CSV (or delimited text) parser
// https://github.com/mholt/PapaParse
const papa = require("papaparse");

const inputs = [
	{ file: "companies", template: companyTemplate },
	{ file: "people", template: personTemplate },
	{ file: "contracts", template: contractTemplate },
	{ file: "calls", template: callTemplate }
];

/**
 * gets the job done:
 * 1. creates an instance of Grakn Client
 * 2. creates a session to the targeted keyspace
 * 3. for each input:
 *      - a. constructs the full path to the data file
 *      - b. loads csv to Grakn
 * 4. closes the session
 * 5. closes the client
 */
async function buildPhoneCallGraph(dataPath, keyspaceName = "phone_calls") {
	const client = new Grakn("localhost:48555"); // 1
	const session = await client.session(keyspaceName); // 2

	for (input of inputs) {
	    console.log(input.file);
	    input.file = input.file.replace(dataPath, ""); // for testing purposes
	    input.file = dataPath + input.file // 3a
		console.log("Loading from [" + input.file + ".csv] into Grakn ...");
		await loadDataIntoGrakn(input, session); // 3b
	}

	await session.close(); // 4
	client.close(); // 5
}

/**
 * loads the csv data into our Grakn phone_calls keyspace
 * @param {object} input contains details required to parse the data
 * @param {object} session a Grakn session, off of which a transaction will be created
 */
async function loadDataIntoGrakn(input, session) {
    const items = await parseDataToObjects(input);

    for (item of items) {
        let transaction;
        transaction = await session.transaction().write();

        const graqlInsertQuery = input.template(item);
        console.log("Executing Graql Query: " + graqlInsertQuery);
        await transaction.query(graqlInsertQuery);
        await transaction.commit();
    }

    console.log(
        `\nInserted ${items.length} items from [${input.file}.csv] into Grakn.\n`
    );
}

function companyTemplate(company) {
	return `insert $company isa company, has name "${company.name}";`;
}

function personTemplate(person) {
	const { first_name, last_name, phone_number, city, age } = person;
	// insert person
	let graqlInsertQuery = `insert $person isa person, has phone-number "${phone_number}"`;

	if (first_name !== "") {
		graqlInsertQuery += `, has first-name "${first_name}"`;
		graqlInsertQuery += `, has last-name "${last_name}"`;
		graqlInsertQuery += `, has city "${city}"`;
		graqlInsertQuery += `, has age ${age}`;
	}

	graqlInsertQuery += ";";
	return graqlInsertQuery;
}

function contractTemplate(contract) {
	const { company_name, person_id } = contract;
	// match company
	let graqlInsertQuery = `match $company isa company, has name "${company_name}"; `;
	// match person
	graqlInsertQuery += `$customer isa person, has phone-number "${person_id}"; `;
	// insert contract
	graqlInsertQuery +=
		"insert (provider: $company, customer: $customer) isa contract;";
	return graqlInsertQuery;
}

function callTemplate(call) {
	const { caller_id, callee_id, started_at, duration } = call;
	// match caller
	let graqlInsertQuery = `match $caller isa person, has phone-number "${caller_id}"; `;
	// match callee
	graqlInsertQuery += `$callee isa person, has phone-number "${callee_id}"; `;
	// insert call
	graqlInsertQuery += `insert $call(caller: $caller, callee: $callee) isa call; $call has started-at ${started_at}; $call has duration ${duration};`;
	return graqlInsertQuery;
}

/**
 * 1. reads the file through a stream,
 * 2. parses the csv line to a json object
 * 3. adds the parsed object to the list of items
 * @param {string} input.file path to the data file
 * @returns items that is, a list of objects each representing a row in the csv file
 */
function parseDataToObjects(input) {
	const items = [];
	return new Promise(function (resolve, reject) {
        papa.parse(
            fs.createReadStream(input.file + ".csv"), // 1
            {
                header: true, // a Papaparse config option
                // 2
                step: function (results, parser) {
                    items.push(results.data[0]); // 3
                },
                complete: function () {
                    resolve(items);
                }
            }
        );
	});
}

module.exports.init = buildPhoneCallGraph;