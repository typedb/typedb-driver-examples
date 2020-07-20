java_library(
    name = "csv-migration-lib",
    srcs = ["CSVMigration.java"],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
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
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson",
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
        "@maven//:org_sharegov_mjson",
        "@maven//:javax_xml_stream_stax_api"
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
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
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
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson",
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
        "@maven//:org_sharegov_mjson",
        "@maven//:javax_xml_stream_stax_api"
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
)
