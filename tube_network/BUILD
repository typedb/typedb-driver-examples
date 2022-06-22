exports_files(["requirements.txt"])

load("@rules_python//python:python.bzl", "py_library", "py_test")

load("@vaticle_typedb_client_python_pip//:requirements.bzl",
       vaticle_typedb_client_python_requirement = "requirement")

py_library(
    name = "migration",
    srcs = ["src/migration.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python"
    ],
    data = glob(["data/*/*.json"]),
)

py_library(
    name = "statistics",
    srcs = ["src/statistics.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python"
    ]
)

py_library(
    name = "journey-planner",
    srcs = ["src/journey_planner.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python"
    ]
)

#py_library(
#    name = "app",
#    srcs = ["src/app.py"],
#    deps = [
#        "@vaticle_typedb_client_python//:client_python"
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
        "@vaticle_typedb_client_python//:client_python",
    ],
    data = [
        "schema.tql"
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
        "@vaticle_typedb_client_python//:client_python",
    ],
    data = [
        "schema.tql"
    ]
)

py_binary(
    name = "app",
    srcs = ["src/app.py"],
#    data = [":transform"],  # a cc_binary which we invoke at run time
    deps = [
        "@vaticle_typedb_client_python//:client_python"
    ]
)
