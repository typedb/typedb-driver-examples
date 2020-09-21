exports_files(["requirements.txt"])

load("@rules_python//python:python.bzl", "py_library", "py_test")

load("@graknlabs_client_python_pip//:requirements.bzl",
       graknlabs_client_python_requirement = "requirement")

load("@phone_calls_pip//:requirements.bzl",
       phone_calls_requirement = "requirement")

py_library(
    name = "migrate-csv",
    srcs = ["migrate_csv.py"],
    deps = [
        "@graknlabs_client_python//:client_python"
    ],
    data = [
        "//datasets:phone-calls-data-csv"
    ]
)

py_library(
    name = "migrate-json",
    srcs = ["migrate_json.py"],
    deps = [
        "@graknlabs_client_python//:client_python",
        phone_calls_requirement("ijson")
    ],
    data = [
        "//datasets:phone-calls-data-json"
    ]
)

py_library(
    name = "migrate-xml",
    srcs = ["migrate_xml.py"],
    deps = [
        "@graknlabs_client_python//:client_python"
    ],
    data = [
        "//datasets:phone-calls-data-xml"
    ]
)

py_library(
    name = "queries",
    srcs = ["queries.py"],
    deps = [
        "@graknlabs_client_python//:client_python"
    ]
)


py_test(
    name = "test",
    main = "test.py",
    srcs = [
        "test.py",
        ":migrate-csv",
        ":migrate-json",
        ":migrate-xml",
        ":queries"
    ],
    deps = [
        "@graknlabs_client_python//:client_python",
    ],
    data = [
        "//schemas:phone-calls-schema.gql"
    ]
)
