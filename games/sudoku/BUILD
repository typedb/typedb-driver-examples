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

load("@rules_python//python:python.bzl", "py_binary", "py_test")
load("@sudoku_pip//:requirements.bzl", sudoku_requirement = "requirement")

# Define targets
py_library(
    name = "solver",
    srcs = ["solver.py"],
    deps = [
        sudoku_requirement("typedb-client")
    ],
    data = [
        ":sudoku6x6_schema.tql",
        ":sudoku6x6_data.tql",
    ]
)

py_test(
    name = "test",
    srcs = ["test.py"],
    deps = [
        "solver",
        sudoku_requirement("typedb-client")q
    ],
    data = [
            "sample/sudoku1.txt",
            "sample/sudoku2.txt",
            "sample/sudoku3.txt",
            "sample/sudoku4.txt",
            "sample/solution1.txt",
            "sample/solution2.txt",
            "sample/solution3.txt",
            "sample/solution4.txt",
            ]
)

# Define checkstyle
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = glob(["*.txt", "*.md", "sample/*"]),
    license_type = "apache-header",
    size = "small",
)
