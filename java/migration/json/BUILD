
java_test(
    name = "test-phone-calls-migration-json-java",
    test_class = "grakn.example.TestPhoneCallsJSONMigration",
    srcs = [
        "TestPhoneCallsJSONMigration.java",
        "src/main/java/grakn/example/PhoneCallsJSONMigration.java"
    ],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/com/google/code/gson:gson",
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test-integration/resources:logback-test",
    ],
    data = [
        "//java:schema.gql",
        "files/phone-calls/data/calls.json",
        "files/phone-calls/data/people.json",
        "files/phone-calls/data/contracts.json",
        "files/phone-calls/data/companies.json"
    ]
)