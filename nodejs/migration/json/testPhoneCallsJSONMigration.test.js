const fs = require("fs");
const GraknClient = require("grakn-client");
const reporters = require("jasmine-reporters");

const jsonMigration = require("./migrate");

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
});

describe("Migration of JSON data into Grakn", function() {
    it("tests migrate.js", async function() {
        await jsonMigration.init();
        await assertResults();
    });
});

async function assertResults() {
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


afterAll(async function() {
    await client.keyspaces().delete("phone_calls");
    console.log("Deleted the phone_calls keyspace");
    await session.close();
    client.close();
});