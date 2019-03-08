exports_files(["requirements.txt"])

load("@io_bazel_rules_python//python:python.bzl", "py_library", "py_test")
load("@pypi_dependencies//:requirements.bzl", "requirement")

py_library(
    name = "migration",
    srcs = ["src/migration.py"],
    deps = [
        "@graknlabs_client_python//:client_python"
    ],
    data = [
        "//datasets:tube-network-data-routes",
        "//datasets:tube-network-data-timetables"
    ]
)

py_library(
    name = "statistics",
    srcs = ["src/statistics.py"],
    deps = [
        "@graknlabs_client_python//:client_python"
    ]
)

py_library(
    name = "journey-planner",
    srcs = ["src/journey_planner.py"],
    deps = [
        "@graknlabs_client_python//:client_python"
    ]
)
#
#py_library(
#    name = "queries",
#    srcs = ["queries.py"],
#    deps = [
#        "@graknlabs_client_python//:client_python"
#    ]
#)


py_test(
    name = "test",
    main = "test.py",
    srcs = [
        "test.py",
        ":migration",
        ":statistics",
        ":journey-planner"
    ],
    deps = [
        "@graknlabs_client_python//:client_python",
    ],
    data = [
        "//schemas:tube-network-schema.gql"
    ]
)