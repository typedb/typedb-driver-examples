java_library(
    name = "csv-migration-lib",
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
    name = "json-migration-lib",
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
    name = "xml-migration-lib",
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
    name = "queries-lib",
    srcs = ["Queries.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
    ]
)

java_test(
    name = "test",
    test_class = "grakn.example.phoneCalls.PhoneCallsTest",
    srcs = [
        "PhoneCallsTest.java"
    ],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        ":csv-migration-lib",
        ":json-migration-lib",
        ":xml-migration-lib",
        ":queries-lib"
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test/resources:logback-test",
    ],
    data = [
        "//schemas:phone-calls-schema.gql"
    ]
)

java_binary(
    name = "csv-migration",
    main_class = "grakn.example.phoneCalls.CSVMigration",
    srcs = ["CSVMigration.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/com/univocity:univocity-parsers"
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test/resources:logback-test",
    ],
    data = [
        "//datasets:phone-calls-data-csv"
    ]
)

java_binary(
    name = "json-migration",
    main_class = "grakn.example.phoneCalls.JSONMigration",
    srcs = ["JSONMigration.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/com/google/code/gson:gson",
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test/resources:logback-test",
    ],
    data = [
        "//datasets:phone-calls-data-json"
    ]
)

java_binary(
    name = "xml-migration",
    main_class = "grakn.example.phoneCalls.XMLMigration",
    srcs = ["XMLMigration.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/javax/xml/stream:stax-api"
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test/resources:logback-test",
    ],
    data = [
        "//datasets:phone-calls-data-xml"
    ]
)

java_binary(
    name = "queries",
    main_class = "grakn.example.phoneCalls.Queries",
    srcs = ["Queries.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test/resources:logback-test",
    ]
)
