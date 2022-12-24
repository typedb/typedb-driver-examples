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

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = glob(["data/*", "*.md", "*.txt"]),
    license_type = "apache-header",
    size = "small",
)

exports_files(["requirements.txt"])

load("@rules_python//python:python.bzl", "py_library", "py_test")

load("@vaticle_typedb_client_python_pip//:requirements.bzl",
       vaticle_typedb_client_python_requirement = "requirement")

load("@bookstore_pip//:requirements.bzl",
       bookstore_requirement = "requirement")

py_binary(
    name = "load_data",
    srcs = ["load_data.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python",

        bookstore_requirement("argparse")

#import loaders
#import config

    ],
    data = [
        "//commerce/bookstore:data-csv"
    ]
)

py_binary(
    name = "requests",
    srcs = ["requests.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python",

        bookstore_requirement("argparse")
    ],
    data = [
        "//commerce/bookstore:data-csv"
    ]
)

py_library(
    name = "loaders",
    srcs = ["loaders.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python",

        bookstore_requirement("uuid")
    ]
)

py_library(
    name = "config",
    srcs = ["config.py"],
    deps = [
        "@vaticle_typedb_client_python//:client_python"
    ]
)
