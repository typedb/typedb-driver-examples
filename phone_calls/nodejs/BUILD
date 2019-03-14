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
        "@phone_calls_npm//jasmine",
        "@phone_calls_npm//jasmine-reporters",
        "@phone_calls_npm//grakn-client",
        "@phone_calls_npm//papaparse",
        "@phone_calls_npm//stream-json",
        "@phone_calls_npm//xml-stream"

    ],
    data = [
        "//schemas:phone-calls-schema.gql",
        "//datasets:phone-calls-data-csv",
        "//datasets:phone-calls-data-json",
        "//datasets:phone-calls-data-xml"
    ]
)