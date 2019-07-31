exports_files(["package.json", "yarn.lock"])

load("@npm_bazel_jasmine//:index.bzl", "jasmine_node_test")

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
        "@npm//jasmine",
        "@npm//jasmine-reporters",
        "@npm//grakn-client",
        "@npm//papaparse",
        "@npm//stream-json",
        "@npm//xml-stream"

    ],
    data = [
        "//schemas:phone-calls-schema.gql",
        "//datasets:phone-calls-data-csv",
        "//datasets:phone-calls-data-json",
        "//datasets:phone-calls-data-xml"
    ]
)