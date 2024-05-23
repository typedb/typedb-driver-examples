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

load("@vaticle_bazel_distribution//artifact:rules.bzl", "artifact_extractor")
load("@vaticle_dependencies//builder/java:rules.bzl", "native_typedb_artifact")

native_typedb_artifact(
    name = "native-typedb-artifact",
    native_artifacts = {
        "@vaticle_bazel_distribution//platform:is_linux_arm64": ["@vaticle_typedb_artifact_linux-arm64//file"],
        "@vaticle_bazel_distribution//platform:is_linux_x86_64": ["@vaticle_typedb_artifact_linux-x86_64//file"],
        "@vaticle_bazel_distribution//platform:is_mac_arm64": ["@vaticle_typedb_artifact_mac-arm64//file"],
        "@vaticle_bazel_distribution//platform:is_mac_x86_64": ["@vaticle_typedb_artifact_mac-x86_64//file"],
        "@vaticle_bazel_distribution//platform:is_windows_x86_64": ["@vaticle_typedb_artifact_windows-x86_64//file"],
    },
    output = "typedb-server-native.tar.gz",
    visibility = ["//test/integration:__subpackages__"],
)

artifact_extractor(
    name = "typedb-extractor",
    artifact = ":native-typedb-artifact",
)

# CI targets that are not declared in any BUILD file, but are called externally
filegroup(
    name = "ci",
    data = [
        "@vaticle_dependencies//library/maven:update",
        "@vaticle_dependencies//distribution/artifact:create-netrc",
        "@vaticle_dependencies//tool/checkstyle:test-coverage",
        "@vaticle_dependencies//tool/sonarcloud:code-analysis",
    ],
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob([".factory/*", "commerce/bookstore/**/*"]) + [".bazelrc", ".gitignore", "BUILD", "WORKSPACE"],
    exclude = glob(["commerce/bookstore/data/*", "commerce/bookstore/images/*", "commerce/bookstore/request-examples/*",
                    "commerce/bookstore/README.md", "commerce/bookstore/python/requirements.txt",
                    "commerce/bookstore/python/todo.md"]),
    license_type = "apache-header",
    size = "small",
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle_fraud",
    include = glob([".factory/*", "finance/fraud_detection/**/*"]) + [".bazelrc", ".gitignore", "BUILD", "WORKSPACE"],
    exclude = glob(["finance/fraud_detection/src/main/java/com/typedb/examples/fraudDetection/db/*", "finance/fraud_detection/src/main/java/com/typedb/examples/fraudDetection/model/*",
                    "finance/fraud_detection/src/main/java/com/typedb/examples/fraudDetection/util/*", "finance/fraud_detection/src/main/java/com/typedb/examples/fraudDetection/web/*",
                    "finance/fraud_detection/src/test/resources/*",
                    "finance/fraud_detection/README.md", "finance/fraud_detection/mvnw",
                    "finance/fraud_detection/pom.xml", "finance/fraud_detection/src/main/resources/META-INF/resources/index.html",
                    "finance/fraud_detection/src/main/resources/application.properties",
                    "finance/fraud_detection/src/main/resources/data.csv",
                    "finance/fraud_detection/src/main/resources/logback.xml"]),
    license_type = "apache-header",
    size = "small",
)

checkstyle_test(
    name = "checkstyle-license",
    include = ["LICENSE"],
    license_type = "apache-fulltext",
    size = "small",
)
