
load("@build_bazel_rules_nodejs//:defs.bzl", "jasmine_node_test")

jasmine_node_test(
    name = "test-phone-calls-migration-csv-nodejs",
    srcs = [
        "testPhoneCallsCSVMigration.test.js",
        "migrate.js"
    ],
    deps = [
        "@nodejs_dependencies//jasmine",
        "@nodejs_dependencies//jasmine-reporters",
        "@nodejs_dependencies//grakn-client",
        "@nodejs_dependencies//papaparse",
    ],
    data = [
        "//nodejs:schema.gql",
        "files/phone-calls/data/calls.csv",
        "files/phone-calls/data/people.csv",
        "files/phone-calls/data/contracts.csv",
        "files/phone-calls/data/companies.csv"
    ]
)

## The following is used by test-phone-calls-queries-nodejs

exports_files(["migrate.js"])

filegroup(
    name = "phone-calls-csv-data",
    srcs = glob(["files/**/*.csv"]),
    visibility = ["//visibility:public"]
)