java_library(
    name = "migration-lib",
    srcs = ["Migration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = glob(["data/*.csv"]),
)

java_library(
    name = "queries-lib",
    srcs = ["Queries.java", "TransactionMode.java", "ResearchTask.java", "InventoryItem.java", "Result.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//:typeql-lang",
    ]
)

java_test(
    name = "test",
    test_class = "com.vaticle.typedb.example.xcom.XCOMTest",
    srcs = [
        "XCOMTest.java"
    ],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        ":migration-lib",
        ":queries-lib"
    ],
    data = [
        "schema.tql"
    ],
    resources = ["//conf:logback-test.xml"],
    resource_strip_prefix = "conf",
    size = "large",
)

java_binary(
    name = "migration",
    main_class = "com.vaticle.typedb.example.xcom.Migration",
    srcs = ["Migration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = glob(["data/*.csv"]),
)

java_binary(
    name = "queries",
    main_class = "com.vaticle.typedb.example.xcom.Queries",
    srcs = ["Queries.java", "TransactionMode.java", "ResearchTask.java", "InventoryItem.java", "Result.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//:typeql-lang",
    ],
)
