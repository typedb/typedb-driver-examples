#kotlin_library(
#    name = "github-migration-lib",
#    data = [
#        "//github/datasets:github-data-json",
#    ],
#    deps = [
#        "@maven//:com_eclipsesource_minimal_json_minimal_json",
#        "@maven//:org_kohsuke_github_api",
#        "@vaticle_typedb_client_java//:client-java",
#        "@vaticle_typedb_client_java//api",
#        "@vaticle_typeql_lang_java//:typeql-lang",
#        "@vaticle_typeql_lang_java//query",
#    ],
#)
#
#java_test(
#    name = "test",
#    test_class = "com.vaticle.typedb.example.Github.Test",
#    srcs = [
#        "Test.java"
#    ],
#    deps = [
#        "@vaticle_typedb_client_java//:client-java",
#        "@vaticle_typeql_lang_java//:typeql-lang",
#        "@vaticle_typedb_client_java//api",
#        "@vaticle_typeql_lang_java//query",
#        ":github-migration-lib",
#    ],
#    data = [
#        "//github/schemas:github-schema.tql"
#    ],
#    resources = ["//conf:logback-test.xml"],
#    resource_strip_prefix = "conf",
#    size = "large",
#)
#
#java_binary(
#    name = "github-migration",
#    data = [
#        "//github/datasets:github-data-json",
#    ],
#    main_class = "com.vaticle.typedb.example.github.github.GithubMigration",
#    deps = [
#        "@maven//:com_eclipsesource_minimal_json_minimal_json",
#        "@maven//:org_kohsuke_github_api",
#        "@vaticle_typedb_client_java//:client-java",
#        "@vaticle_typedb_client_java//api",
#        "@vaticle_typeql_lang_java//:typeql-lang",
#        "@vaticle_typeql_lang_java//query",
#    ],
#)

java_binary(
    name = "github-bin-mac",
    data = [
        "//github/datasets:github-data-json",
        "//github/schemas:github-data-schemas",
    ],
    main_class = "github.view.GitApplication",
    runtime_deps = [
        "//github/view",
        "@maven//:org_jetbrains_skiko_skiko_awt_runtime_macos_x64",
    ],
)

java_binary(
    name = "github-bin-windows",
    data = [
        "//github/datasets:github-data-json",
        "//github/schemas:github-data-schemas",
    ],
    main_class = "github.view.GitApplication",
    runtime_deps = [
        "//github/view",
        "@maven//:org_jetbrains_skiko_skiko_awt_runtime_windows_x64",
    ],
)

java_binary(
    name = "github-bin-linux",
    data = [
        "//github/datasets:github-data-json",
        "//github/schemas:github-data-schemas",
    ],
    main_class = "github.view.GitApplication",
    runtime_deps = [
        "//github/view",
        "@maven//:org_jetbrains_skiko_skiko_awt_runtime_linux_x64",
    ],
)
