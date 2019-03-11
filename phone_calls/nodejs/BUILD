exports_files(["package.json"])

load("@build_bazel_rules_nodejs//:defs.bzl", "jasmine_node_test", "nodejs_binary")

jasmine_node_test(
    name = "test",
    srcs = [
        "test.js",
        "migrateCsv.js",
        "migrateJson.js",
        "migrateXml.js",
        "queries.js"
    ],
    deps = [
        "@nodejs_dependencies//jasmine",
        "@nodejs_dependencies//jasmine-reporters",
        "@nodejs_dependencies//grakn-client",
        "@nodejs_dependencies//papaparse",
        "@nodejs_dependencies//stream-json",
        "@nodejs_dependencies//xml-stream"

    ],
    data = [
        "//schemas:phone-calls-schema.gql",
        "//datasets:phone-calls-data-csv",
        "//datasets:phone-calls-data-json",
        "//datasets:phone-calls-data-xml"
    ]
)