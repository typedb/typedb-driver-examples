load("@io_bazel_rules_python//python:python.bzl", "py_library", "py_test")
load("@pypi_dependencies//:requirements.bzl", "requirement")

py_library(
    name = "python-library-phone-calls-migration-csv",
    deps = [
        "@graknlabs_client_python//:client_python",
    ]
)

py_test(
    name = "test-phone-calls-migration-csv-python",
    main = "test_phone_calls_csv_migration.py",
    srcs = [
        "test_phone_calls_csv_migration.py",
        "migrate.py"
    ],
    deps = [
        "python-library-phone-calls-migration-csv",
    ],
    data = [
        "//python:schema.gql",
        "files/phone-calls/data/calls.csv",
        "files/phone-calls/data/people.csv",
        "files/phone-calls/data/contracts.csv",
        "files/phone-calls/data/companies.csv"
    ]
)

## The following is used by test-phone-calls-queries-python

exports_files(["migrate.py"])

filegroup(
    name = "phone-calls-csv-data",
    srcs = glob(["files/**/*.csv"]),
    visibility = ["//visibility:public"]
)