load("@build_bazel_rules_nodejs//:defs.bzl", "jasmine_node_test")

jasmine_node_test(
    name = "test-phone-calls-migration-xml-nodejs",
    srcs = [
        "testPhoneCallsXMLMigration.test.js",
        "migrate.js"
    ],
    deps = [
        "@nodejs_dependencies//jasmine",
        "@nodejs_dependencies//jasmine-reporters",
        "@nodejs_dependencies//grakn-client",
        "@nodejs_dependencies//xml-stream",
    ],
    data = [
        "//nodejs:schema.gql",
        "files/phone-calls/data/calls.xml",
        "files/phone-calls/data/people.xml",
        "files/phone-calls/data/contracts.xml",
        "files/phone-calls/data/companies.xml"
    ]
)