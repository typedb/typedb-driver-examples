java_library(
    name = "flight-migration-lib",
    srcs = ["FlightMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson"
    ],
    data = [
        "//datasets:flights-data-json",
    ]
)

java_test(
    name = "test",
    test_class = "com.vaticle.typedb.example.flights.Test",
    srcs = [
        "Test.java"
    ],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        ":flight-migration-lib",
    ],
    data = [
        "//flights//schemas:flight-schema.tql"
    ],
    resources = ["//conf:logback-test.xml"],
    resource_strip_prefix = "conf",
    size = "large",
)

java_binary(
    name = "flight-migration",
    main_class = "com.vaticle.typedb.example.flights.FlightMigration",
    srcs = ["FlightMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson"
    ],
    data = [
        "//datasets:flights-data-json",
    ]
)