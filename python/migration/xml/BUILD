load("@io_bazel_rules_python//python:python.bzl", "py_library", "py_test")
load("@pypi_dependencies//:requirements.bzl", "requirement")

py_library(
    name = "python-library-phone-calls-migration-xml",
    deps = [
        "@graknlabs_client_python//:client_python",
    ]
)

py_test(
    name = "test-phone-calls-migration-xml-python",
    main = "test_phone_calls_xml_migration.py",
    srcs = [
        "test_phone_calls_xml_migration.py",
        "migrate.py"
    ],
    deps = [
        "python-library-phone-calls-migration-xml",
    ],
    data = [
        "//python:schema.gql",
        "files/phone-calls/data/calls.xml",
        "files/phone-calls/data/people.xml",
        "files/phone-calls/data/contracts.xml",
        "files/phone-calls/data/companies.xml"
    ]
)