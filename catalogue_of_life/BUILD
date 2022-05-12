java_binary(
    name = "loader",
    main_class = "com.vaticle.typedb.example.catalogueOfLife.Loader",
    srcs = ["Loader.java"],
    deps = [
        "@maven//:com_univocity_univocity_parsers",
        "@maven//:info_picocli_picocli",
        "@typedb-loader//:com_vaticle_typedb_osi_typedb_loader",
    ],
    resources = [
        ":schema.tql",
        ":loader-config.json.template",
    ],
)

java_test(
    name = "tests",
    test_class = "com.vaticle.typedb.example.catalogueOfLife.test.CoLTest",
    srcs = [
        "test/CoLTest.java"
    ],
    deps = [
        ":loader",
        "@maven//:com_univocity_univocity_parsers",
        "@maven//:info_picocli_picocli",
        "@typedb-loader//:com_vaticle_typedb_osi_typedb_loader",
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql_lang_java//:typeql-lang",
    ],
    data = glob(["test/data/*.tsv"]),
)
