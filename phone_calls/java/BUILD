java_library(
    name = "csv-migration-lib",
    srcs = ["CSVMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typedb_client_java//api",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = [
        "//phone_calls:data-csv",
    ]
)

java_library(
    name = "json-migration-lib",
    srcs = ["JSONMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson",
    ],
    data = [
        "//phone_calls:data-json",
    ]
)

java_library(
    name = "xml-migration-lib",
    srcs = ["XMLMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typedb_client_java//api",
        "@maven//:org_sharegov_mjson",
        "@maven//:javax_xml_stream_stax_api"
    ],
    data = [
        "//phone_calls:data-xml",
    ]
)

java_library(
    name = "queries-lib",
    srcs = ["Queries.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typedb_client_java//api",
    ]
)

java_test(
    name = "test",
    test_class = "com.vaticle.typedb.example.phoneCalls.PhoneCallsTest",
    srcs = [
        "PhoneCallsTest.java"
    ],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        ":csv-migration-lib",
        ":json-migration-lib",
        ":xml-migration-lib",
        ":queries-lib"
    ],
    data = [
        "//phone_calls:schema.tql",
    ],
    resources = ["//conf:logback-test.xml"],
    resource_strip_prefix = "conf",
)

java_binary(
    name = "csv-migration",
    main_class = "com.vaticle.typedb.example.phoneCalls.CSVMigration",
    srcs = ["CSVMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//query",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = [
        "//phone_calls:data-csv",
    ]
)

java_binary(
    name = "json-migration",
    main_class = "com.vaticle.typedb.example.phoneCalls.JSONMigration",
    srcs = ["JSONMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson",
    ],
    data = [
        "//phone_calls:data-json",
    ]
)

java_binary(
    name = "xml-migration",
    main_class = "com.vaticle.typedb.example.phoneCalls.XMLMigration",
    srcs = ["XMLMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typedb_client_java//api",
        "@maven//:org_sharegov_mjson",
        "@maven//:javax_xml_stream_stax_api"
    ],
    data = [
        "//phone_calls:data-xml",
    ]
)

java_binary(
    name = "queries",
    main_class = "com.vaticle.typedb.example.phoneCalls.Queries",
    srcs = ["Queries.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typeql_lang_java//query",
        "@vaticle_typedb_client_java//api",
    ],
)
