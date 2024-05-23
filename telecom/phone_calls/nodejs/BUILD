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

exports_files(["package.json"])

load("@npm//:defs.bzl", "npm_link_all_packages")
npm_link_all_packages(
    name = "node_modules",
)

load("@aspect_rules_jasmine//jasmine:defs.bzl", "jasmine_test")

jasmine_test(
    name = "test",
    args = ["test.js"],
    data = [
        "test.js",
        "migrateCsv.js",
        "migrateJson.js",
        "migrateXml.js",
        "queries.js",
        "package.json",
        "//telecom/phone_calls:schema.tql",
        "//telecom/phone_calls:data-csv",
        "//telecom/phone_calls:data-json",
        "//telecom/phone_calls:data-xml",
#        ":node_modules/jasmine-reporters",
#        ":node_modules/typedb-driver",
#        ":node_modules/papaparse",
#        ":node_modules/stream-json",
#        ":node_modules/xml-stream"
    ],
    chdir = package_name(),
    node_modules = "//:node_modules",
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = glob(["*.json", "*.lock", "*.md"]),
    license_type = "apache-header",
    size = "small",
)
