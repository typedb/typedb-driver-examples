load("@io_bazel_rules_python//python:python.bzl", "py_library", "py_test")
load("@pypi_dependencies//:requirements.bzl", "requirement")

py_library(
    name = "python-library-phone-calls-queries",
    deps = [
        "@graknlabs_client_python//:client_python",
    ]
)

py_test(
    name = "test-phone-calls-queries-python",
    main = "test_phone_calls_queries.py",
    srcs = [
        "test_phone_calls_queries.py",
        "queries.py",
        "//python/migration/csv:migrate.py"
    ],
    deps = [
        "python-library-phone-calls-queries",
    ],
    data = [
        "//python:schema.gql",
        "//python/migration/csv:phone-calls-csv-data"
    ]
)