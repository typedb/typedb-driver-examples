
java_test(
    name = "test-phone-calls-migration-csv-java",
    test_class = "grakn.example.TestPhoneCallsCSVMigration",
    srcs = [
        "TestPhoneCallsCSVMigration.java",
        "src/main/java/grakn/example/PhoneCallsCSVMigration.java"
    ],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/com/univocity:univocity-parsers",
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test-integration/resources:logback-test",
    ],
    data = [
        "//java:schema.gql",
        "files/phone-calls/data/calls.csv",
        "files/phone-calls/data/people.csv",
        "files/phone-calls/data/contracts.csv",
        "files/phone-calls/data/companies.csv"
    ]
)