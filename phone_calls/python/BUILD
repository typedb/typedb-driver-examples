#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

exports_files(["requirements.txt"])

load("@rules_python//python:python.bzl", "py_library", "py_test")

load("@vaticle_typedb_client_python_pip//:requirements.bzl",
       vaticle_typedb_client_python_requirement = "requirement")

load("@phone_calls_pip//:requirements.bzl",
       phone_calls_requirement = "requirement")

py_library(
    name = "migrate-csv",
    srcs = ["migrate_csv.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python"
    ],
    data = [
        "//phone_calls:data-csv"
    ]
)

py_library(
    name = "migrate-json",
    srcs = ["migrate_json.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python",
        phone_calls_requirement("ijson")
    ],
    data = [
        "//phone_calls:data-json"
    ]
)

py_library(
    name = "migrate-xml",
    srcs = ["migrate_xml.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python"
    ],
    data = [
        "//phone_calls:data-xml"
    ]
)

py_library(
    name = "queries",
    srcs = ["queries.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python"
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
        "@vaticle_typedb_client_python//:client_python",
        phone_calls_requirement("ijson"),
    ],
    data = [
        "//phone_calls:schema.tql"
    ]
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = glob(["*.txt", "*.md"]),
    license_type = "apache-header",
    size = "small",
)
