java_test(
    name = "test-phone-calls-queries-java",
    test_class = "grakn.example.TestPhoneCallsQueries",
    srcs = [
        "TestPhoneCallsQueries.java",
        "src/main/java/grakn/example/PhoneCallsQueries.java"
    ],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "@graknlabs_grakn_core//concept:concept",
        "//java/migration/csv:phone-calls-migration-csv-java"
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test-integration/resources:logback-test"
    ],
    data = [
        "//java:schema.gql",
        "//java/migration/csv:phone-calls-data-csv"
    ]
)