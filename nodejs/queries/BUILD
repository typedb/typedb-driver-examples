load("@build_bazel_rules_nodejs//:defs.bzl", "jasmine_node_test")

jasmine_node_test(
    name = "test-phone-calls-queries-nodejs",
    srcs = [
        "testPhoneCallsQueries.test.js",
        "queries.js",
        "//nodejs/migration/csv:migrate.js"
    ],
    deps = [
        "@nodejs_dependencies//jasmine",
        "@nodejs_dependencies//jasmine-reporters",
        "@nodejs_dependencies//grakn-client",
        "@nodejs_dependencies//papaparse",
    ],
    data = [
        "//nodejs:schema.gql",
        "//nodejs/migration/csv:phone-calls-csv-data"
    ]
)