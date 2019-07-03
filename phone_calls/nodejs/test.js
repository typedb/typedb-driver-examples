const fs = require("fs");
const GraknClient = require("grakn-client");
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
const dataPath = "datasets/phone-calls/"
const keyspaceName = "phone_calls_nodejs"

beforeEach(async function() {
    client = new GraknClient("localhost:48555");
    session = await client.session(keyspaceName);
    const transaction = await session.transaction().write();
    const defineQuery = fs.readFileSync("schemas/phone-calls-schema.gql", "utf8");
    await transaction.query(defineQuery);
    await transaction.commit();
    console.log("Loaded the phone_calls_nodejs schema");
});

describe("Migration of data into Grakn", function() {
    it("tests migrateCsv.js", async function() {
        await csvMigration.init(dataPath, keyspaceName);
        await assertMigrationResults();
    });

    it("tests migrateJson.js", async function() {
        await jsonMigration.init(dataPath, keyspaceName);
        await assertMigrationResults();
    });

    it("tests migrateXml.js", async function() {
        await xmlMigration.init(dataPath, keyspaceName);
        await assertMigrationResults(dataPath);
    });
});

describe("Queries on phone_calls_nodejs keyspace", function() {
    it("tests queries.js", async function() {
        queries.processSelection(0, keyspaceName);

        await csvMigration.init(dataPath, keyspaceName);

        transaction = await session.transaction().read()

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
    transaction = await session.transaction().read();

    let numberOfPeople = await transaction.query("match $x isa person; get $x; count;");
    numberOfPeople = await numberOfPeople.next();
    numberOfPeople = numberOfPeople.number();
    expect(numberOfPeople).toEqual(30);

    let numberOfCompanies = await transaction.query("match $x isa company; get $x; count;");
    numberOfCompanies = await numberOfCompanies.next();
    numberOfCompanies = numberOfCompanies.number();
    expect(numberOfCompanies).toEqual(1);

    let numberOfContracts = await transaction.query("match $x isa contract; get $x; count;");
    numberOfContracts = await numberOfContracts.next();
    numberOfContracts = numberOfContracts.number();
    expect(numberOfContracts).toEqual(10);

    let numberOfCalls = await transaction.query("match $x isa call; get $x; count;");
    numberOfCalls = await numberOfCalls.next();
    numberOfCalls = numberOfCalls.number();
    expect(numberOfCalls).toEqual(200);

    await transaction.close();
}


afterEach(async function() {
    await client.keyspaces().delete(keyspaceName);
    console.log("Deleted the phone_calls_nodejs keyspace");
    await session.close();
    client.close();
});