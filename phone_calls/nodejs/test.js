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

const fs = require("fs");
const { TypeDB } = require("typedb-client/TypeDB");
const { SessionType } = require("typedb-client/api/connection/TypeDBSession");
const { TransactionType } = require("typedb-client/api/connection/TypeDBTransaction");
const reporters = require("jasmine-reporters");

const csvMigration = require("./migrateCsv");
const jsonMigration = require("./migrateJson");
const xmlMigration = require("./migrateXml");
const queries = require("./queries");

const tapReporter = new reporters.TapReporter();
jasmine.getEnv().addReporter(tapReporter);

jasmine.DEFAULT_TIMEOUT_INTERVAL = 100000;

let client;
let session;
const dataPath = "phone_calls/data/"
const databaseName = "phone_calls_nodejs"

beforeEach(async function() {
    client = TypeDB.coreClient("localhost:1729");
    if (!await (client.databases().contains(databaseName))) {
        await client.databases().create(databaseName);
    } else {
        await (await client.databases().get(databaseName)).delete();
        await client.databases().create(databaseName);
    }
    session = await client.session(databaseName, SessionType.SCHEMA);
    const transaction = await session.transaction(TransactionType.WRITE);
    const defineQuery = fs.readFileSync("phone_calls/schema.tql", "utf8");
    await transaction.query().define(defineQuery);
    await transaction.commit();
    await session.close()
    session = await client.session(databaseName, SessionType.DATA);
    console.log("Loaded the phone_calls_nodejs schema");
});

describe("Migration of data into TypeDB", function() {
    it("tests migrateCsv.js", async function() {
        await csvMigration.init(dataPath, databaseName);
        await assertMigrationResults();
    });

    it("tests migrateJson.js", async function() {
        await jsonMigration.init(dataPath, databaseName);
        await assertMigrationResults();
    });

    it("tests migrateXml.js", async function() {
        await xmlMigration.init(dataPath, databaseName);
        await assertMigrationResults(dataPath);
    });
});

describe("Queries on phone_calls_nodejs database", function() {
    it("tests queries.js", async function() {
        await csvMigration.init(dataPath, databaseName);

        queries.processSelection(0, databaseName);

        transaction = await session.transaction(TransactionType.READ)

        const firstActualAnswer = await queries.queryExamples[0].queryFunction("", transaction);
        firstActualAnswer.sort();
        const firstExpectedAnswer = [ "+370 351 224 5176", "+54 398 559 0423", "+62 107 530 7500", "+63 815 962 6097",
                                      "+7 690 597 4443", "+263 498 495 0617", "+81 308 988 7153", "+81 746 154 2598" ]
        firstExpectedAnswer.sort();
        expect(firstActualAnswer).toEqual(firstExpectedAnswer);


        const secondActualAnswer = await queries.queryExamples[1].queryFunction("", transaction);
        secondActualAnswer.sort();
        const secondExpectedAnswer = [ "+351 272 414 6570", "+30 419 575 7546", "+1 254 875 4647", "+86 892 682 0628",
                                       "+33 614 339 0298", "+351 515 605 7915", "+86 922 760 0418", "+63 808 497 1769",
                                       "+86 825 153 5518", "+48 894 777 5173", "+27 117 258 4149", "+86 202 257 8619" ]
        secondExpectedAnswer.sort();
        expect(secondActualAnswer).toEqual(secondExpectedAnswer);


        const thirdActualAnswer = await queries.queryExamples[2].queryFunction("", transaction);
        thirdActualAnswer.sort();
        const thirdExpectedAnswer = [ "+86 892 682 0628", "+54 398 559 0423" ]
        thirdExpectedAnswer.sort();
        expect(thirdActualAnswer).toEqual(thirdExpectedAnswer);


        const forthActualAnswer = await queries.queryExamples[3].queryFunction("", transaction);
        forthActualAnswer.sort();
        const forthExpectedAnswer = [ "+62 107 530 7500", "+81 308 988 7153", "+261 860 539 4754" ]
        forthExpectedAnswer.sort();
        expect(forthActualAnswer).toEqual(forthExpectedAnswer);


        const fifthActualAnswer = await queries.queryExamples[4].queryFunction("", transaction);
        const fifthExpectedAnswer = [ 1242.7714285714285, 1699.4308943089432 ]
        expect(fifthActualAnswer).toEqual(fifthExpectedAnswer);

        await transaction.close()
    });
});

async function assertMigrationResults() {
    transaction = await session.transaction(TransactionType.READ);

    let numberOfPeople = await transaction.query().matchAggregate("match $x isa person; get $x; count;");
    numberOfPeople = await numberOfPeople.asNumber();
    expect(numberOfPeople).toEqual(30);

    let numberOfCompanies = await transaction.query().matchAggregate("match $x isa company; get $x; count;");
    numberOfCompanies = await numberOfCompanies.asNumber();
    expect(numberOfCompanies).toEqual(1);

    let numberOfContracts = await transaction.query().matchAggregate("match $x isa contract; get $x; count;");
    numberOfContracts = await numberOfContracts.asNumber();
    expect(numberOfContracts).toEqual(10);

    let numberOfCalls = await transaction.query().matchAggregate("match $x isa call; get $x; count;");
    numberOfCalls = await numberOfCalls.asNumber();
    expect(numberOfCalls).toEqual(200);

    await transaction.close();
}


afterEach(async function() {
    await session.close();
    await (await client.databases().get(databaseName)).delete();
    console.log("Deleted the phone_calls_nodejs database");
    client.close();
});
