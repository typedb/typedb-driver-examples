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
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_test")

package(default_visibility = ["//software/github:__pkg__"])

kt_jvm_test(
    name = "test-migrator-explorer",
    size = "large",
    srcs = glob(["*.kt"]),
    data = [
        "//software/github/datasets:github-data-json",
        "//software/github/schemas:github-data-schemas",
    ],
    tags = ["maven_coordinates=com.vaticle.typedb:typedb-studio-view:{pom_version}"],
    test_class = "com.vaticle.typedb.example.software.github.tests.MigratorExplorerTest",
    deps = [
        "//software/github/state:state",
        "@maven//:junit_junit",
    ],
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "apache-header",
)
