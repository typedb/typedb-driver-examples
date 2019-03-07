java_library(
    name = "csv-migration",
    srcs = ["CSVMigration.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/com/univocity:univocity-parsers"
    ],
    data = [
        "//datasets:phone-calls-data-csv"
    ]
)

java_library(
    name = "json-migration",
    srcs = ["JSONMigration.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/com/google/code/gson:gson",
    ],
    data = [
        "//datasets:phone-calls-data-json"
    ]
)

java_library(
    name = "xml-migration",
    srcs = ["XMLMigration.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/javax/xml/stream:stax-api"
    ],
    data = [
        "//datasets:phone-calls-data-xml"
    ]
)

java_library(
    name = "queries",
    srcs = ["Queries.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "@graknlabs_grakn_core//concept:concept"
    ]
)

java_test(
    name = "test",
    test_class = "grakn.example.phoneCalls.Test",
    srcs = [
        "Test.java"
    ],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        ":csv-migration",
        ":json-migration",
        ":xml-migration",
        ":queries"
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test-integration/resources:logback-test",
    ],
    data = [
        "//schemas:phone-calls-schema.gql",
    ]
)