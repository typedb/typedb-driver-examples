load("@build_bazel_rules_nodejs//:defs.bzl", "jasmine_node_test")

jasmine_node_test(
    name = "test-phone-calls-migration-json-nodejs",
    srcs = [
        "testPhoneCallsJSONMigration.test.js",
        "migrate.js"
    ],
    deps = [
        "@nodejs_dependencies//jasmine",
        "@nodejs_dependencies//jasmine-reporters",
        "@nodejs_dependencies//grakn-client",
        "@nodejs_dependencies//stream-json",
    ],
    data = [
        "//nodejs:schema.gql",
        "files/phone-calls/data/calls.json",
        "files/phone-calls/data/people.json",
        "files/phone-calls/data/contracts.json",
        "files/phone-calls/data/companies.json"
    ]
)