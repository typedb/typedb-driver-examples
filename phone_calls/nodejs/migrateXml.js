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

// the Node.js client for TypeDB
// https://github.com/vaticle/typedb/tree/master/client-nodejs
const { TypeDB } = require("typedb-client/TypeDB");
const { SessionType } = require("typedb-client/api/connection/TypeDBSession");
const { TransactionType } = require("typedb-client/api/connection/TypeDBTransaction");

// used for creating a stream to read the data files
// https://nodejs.org/api/fs.html#fs_fs_createreadstream_path_options
const fs = require("fs");

// xml stream parser
// https://github.com/assistunion/xml-stream
const xmlStream = require("xml-stream");

const inputs = [
    {
        file: "companies",
        template: companyTemplate,
        selector: "company"
    },
    {
        file: "people",
        template: personTemplate,
        selector: "person"
    },
    {
        file: "contracts",
        template: contractTemplate,
        selector: "contract"
    },
    {
        file: "calls",
        template: callTemplate,
        selector: "call"
    }
];

/**
 * gets the job done:
 * 1. creates an instance of TypeDB Client
 * 2. creates a session to the targeted database
 * 3. for each input,
 *      - a. constructs the full path to the data file
 *      - b. loads csv to TypeDB
 * 4. closes the session
 * 5. closes the client
 */
async function buildPhoneCallGraph(dataPath, database = "phone_calls") {
    const client = TypeDB.coreClient("localhost:1729"); // 1
    const session = await client.session(database, SessionType.DATA); // 2

    for (input of inputs) {
        input.file = input.file.replace(dataPath, "") // for testing purposes
        input.file = dataPath + input.file // 3a
        console.log("Loading from [" + input.file + ".xml] into TypeDB ...");
        await loadDataIntoTypeDB(input, session); // 3b
    }

    await session.close(); // 4
    client.close(); // 5
}

/**
 * loads the xml data into our TypeDB phone_calls database
 * @param {object} input contains details required to parse the data
 * @param {object} session a TypeDB session, off of which a transaction will be created
 */
async function loadDataIntoTypeDB(input, session) {
    const items = await parseDataToObjects(input);

    for (item of items) {
        const transaction = await session.transaction(TransactionType.WRITE);

        const typeQLInsertQuery = input.template(item);
        console.log("Executing TypeQL Query: " + typeQLInsertQuery);
        await transaction.query().insert(typeQLInsertQuery);
        await transaction.commit();
    }

    console.log(
        `\nInserted ${items.length} items from [${input.file}.xml] into TypeDB.\n`
    );
}

function companyTemplate(company) {
    return `insert $company isa company, has name "${company.name}";`;
}

function personTemplate(person) {
    const { first_name, last_name, phone_number, city, age } = person;
    // insert person
    let typeQLInsertQuery = `insert $person isa person, has phone-number "${phone_number}"`;

    if (typeof first_name !== "undefined") {
        typeQLInsertQuery += `, has first-name "${first_name}"`;
        typeQLInsertQuery += `, has last-name "${last_name}"`;
        typeQLInsertQuery += `, has city "${city}"`;
        typeQLInsertQuery += `, has age ${age}`;
    }

    typeQLInsertQuery += ";";
    return typeQLInsertQuery;
}

function contractTemplate(contract) {
    const { company_name, person_id } = contract;
    // match company
    let typeQLInsertQuery = `match $company isa company, has name "${company_name}"; `;
    // match person
    typeQLInsertQuery += `$customer isa person, has phone-number "${person_id}"; `;
    // insert contract
    typeQLInsertQuery +=
        "insert (provider: $company, customer: $customer) isa contract;";
    return typeQLInsertQuery;
}

function callTemplate(call) {
    const { caller_id, callee_id, started_at, duration } = call;
    // match caller
    let typeQLInsertQuery = `match $caller isa person, has phone-number "${caller_id}"; `;
    // match callee
    typeQLInsertQuery += `$callee isa person, has phone-number "${callee_id}"; `;
    // insert call
    typeQLInsertQuery += `insert $call(caller: $caller, callee: $callee) isa call; $call has started-at ${started_at}; $call has duration ${duration};`;
    return typeQLInsertQuery;
}

/**
 * 1. reads the file through a stream,
 * 2. parses the xml element to a json object
 * 3. adds it to items
 * @param {string} input.file path to the data file
 * @param {string} input.selector an xml-stream option to determine the main selector to be parsed
 * @returns items that is, a list of objects each representing a data item the TypeDB database
 */
function parseDataToObjects(input) {
    const items = [];
    return new Promise((resolve, reject) => {
        const pipeline = new xmlStream(
            fs.createReadStream(input.file + ".xml") // 1
        );
        // 2
        pipeline.on(`endElement: ${input.selector}`, function (result) {
            items.push(result); // 3
        });

        pipeline.on("end", () => {
            resolve(items);
        });
    });
}

module.exports.init = buildPhoneCallGraph;
