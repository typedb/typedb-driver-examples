const fs = require("fs");
const GraknClient = require("grakn-client");
const reporters = require("jasmine-reporters");

const csvMigration = require("../migration/csv/migrate");
const queries = require("./queries");

const tapReporter = new reporters.TapReporter();
jasmine.getEnv().addReporter(tapReporter);

jasmine.DEFAULT_TIMEOUT_INTERVAL = 100000;

let client;
let session;

beforeAll(async function() {
    client = new GraknClient("localhost:48555");
    session = await client.session("phone_calls");
    const transaction = await session.transaction().write();
    const defineQuery = fs.readFileSync("nodejs/schema.gql", "utf8");
    await transaction.query(defineQuery);
    await transaction.commit();
    console.log("Loaded the phone_calls schema");
    await csvMigration.init();
    console.log("Loaded the phone_calls data");
});

describe("Queries on the phone_calls keyspace", function() {
    it("tests queries.js", async function() {
        const firstActualAnswer = await queries.queryExamples[0].queryFunction("", await session.transaction().read());
        firstActualAnswer.sort();
        const firstExpectedAnswer = [ "+370 351 224 5176", "+54 398 559 0423", "+62 107 530 7500", "+63 815 962 6097",
                                      "+7 690 597 4443", "+263 498 495 0617", "+81 308 988 7153", "+81 746 154 2598" ]
        firstExpectedAnswer.sort();
        expect(firstActualAnswer).toEqual(firstExpectedAnswer);


        const secondActualAnswer = await queries.queryExamples[1].queryFunction("", await session.transaction().read());
        secondActualAnswer.sort();
        const secondExpectedAnswer = [ "+351 272 414 6570", "+30 419 575 7546", "+1 254 875 4647", "+86 892 682 0628",
                                       "+33 614 339 0298", "+351 515 605 7915", "+86 922 760 0418", "+63 808 497 1769",
                                       "+86 825 153 5518", "+48 894 777 5173", "+27 117 258 4149", "+86 202 257 8619" ]
        secondExpectedAnswer.sort();
        expect(secondActualAnswer).toEqual(secondExpectedAnswer);


        const thirdActualAnswer = await queries.queryExamples[2].queryFunction("", await session.transaction().read());
        thirdActualAnswer.sort();
        const thirdExpectedAnswer = [ "+86 892 682 0628", "+54 398 559 0423" ]
        thirdExpectedAnswer.sort();
        expect(thirdActualAnswer).toEqual(thirdExpectedAnswer);


        const forthActualAnswer = await queries.queryExamples[3].queryFunction("", await session.transaction().read());
        forthActualAnswer.sort();
        const forthExpectedAnswer = [ "+62 107 530 7500", "+81 308 988 7153", "+261 860 539 4754" ]
        forthExpectedAnswer.sort();
        expect(forthActualAnswer).toEqual(forthExpectedAnswer);


        const fifthActualAnswer = await queries.queryExamples[4].queryFunction("", await session.transaction().read());
        const fifthExpectedAnswer = [ 1242.7714285714285, 1699.4308943089432 ]
        expect(fifthActualAnswer).toEqual(fifthExpectedAnswer);

    });
});

afterAll(async function() {
    await client.keyspaces().delete("phone_calls");
    console.log("Deleted the phone_calls keyspace");
    await session.close();
    client.close();
});