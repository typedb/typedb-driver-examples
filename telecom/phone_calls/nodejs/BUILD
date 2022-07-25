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

exports_files(["package.json", "yarn.lock"])

load("@npm//@bazel/jasmine:index.bzl", "jasmine_node_test")

jasmine_node_test(
    name = "test",
    srcs = [
        "test.js",
        "migrateCsv.js",
        "migrateJson.js",
        "migrateXml.js",
        "queries.js"
    ],
    deps = [
        "@npm//jasmine",
        "@npm//jasmine-reporters",
        "@npm//typedb-client",
        "@npm//papaparse",
        "@npm//stream-json",
        "@npm//xml-stream"

    ],
    data = [
        "//telecom/phone_calls:schema.tql",
        "//telecom/phone_calls:data-csv",
        "//telecom/phone_calls:data-json",
        "//telecom/phone_calls:data-xml",
    ]
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = glob(["*.json", "*.lock", "*.md"]),
    license_type = "apache-header",
    size = "small",
)
