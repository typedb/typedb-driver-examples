exports_files(["requirements.txt"])

load("@io_bazel_rules_python//python:python.bzl", "py_library", "py_test")
load("@pypi_dependencies//:requirements.bzl", "requirement")
load("@phone_calls_migration_json_pypi_dependencies//:requirements.bzl", "requirement")

py_library(
    name = "python-library-phone-calls-migration-json",
    deps = [
        "@graknlabs_client_python//:client_python",
        requirement("ijson")
    ]
)

py_test(
    name = "test-phone-calls-migration-json-python",
    main = "test_phone_calls_json_migration.py",
    srcs = [
        "test_phone_calls_json_migration.py",
        "migrate.py"
    ],
    deps = [
        "python-library-phone-calls-migration-json",
    ],
    data = [
        "//python:schema.gql",
        "files/phone-calls/data/calls.json",
        "files/phone-calls/data/people.json",
        "files/phone-calls/data/contracts.json",
        "files/phone-calls/data/companies.json"
    ]
)