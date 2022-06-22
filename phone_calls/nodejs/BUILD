exports_files(["package.json", "yarn.lock"])

load("@npm//@bazel/jasmine:index.bzl", "jasmine_node_test")

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
        "@npm//typedb-client",
        "@npm//papaparse",
        "@npm//stream-json",
        "@npm//xml-stream"

    ],
    data = [
        "//phone_calls:schema.tql",
        "//phone_calls:data-csv",
        "//phone_calls:data-json",
        "//phone_calls:data-xml",
    ]
)
