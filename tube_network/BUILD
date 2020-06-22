exports_files(["requirements.txt"])

load("@rules_python//python:python.bzl", "py_library", "py_test")

load("@graknlabs_client_python_pip//:requirements.bzl",
       graknlabs_client_python_requirement = "requirement")

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

#py_library(
#    name = "app",
#    srcs = ["src/app.py"],
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
        ":journey-planner",
        ":app"
    ],
    timeout = "long",
    deps = [
        "@graknlabs_client_python//:client_python",
    ],
    data = [
        "//schemas:tube-network-schema.gql"
    ]
)

py_test(
    name = "test-migration",
    main = "test_migration.py",
    srcs = [
        "test_migration.py",
        ":migration",
    ],
    deps = [
        "@graknlabs_client_python//:client_python",
    ],
    data = [
        "//schemas:tube-network-schema.gql"
    ]
)

py_binary(
    name = "app",
    srcs = ["src/app.py"],
#    data = [":transform"],  # a cc_binary which we invoke at run time
    deps = [
        "@graknlabs_client_python//:client_python"
    ]
)