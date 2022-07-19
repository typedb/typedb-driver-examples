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

java_library(
    name = "migration-lib",
    srcs = ["Migration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java/query",
        "@vaticle_typeql//java:typeql-lang",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = glob(["data/*.csv"]) + ["schema.tql"],
)

java_library(
    name = "queries-lib",
    srcs = ["Queries.java", "TransactionMode.java", "ResearchTask.java", "InventoryItem.java", "Result.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java:typeql-lang",
    ]
)

java_test(
    name = "test",
    classpath_resources = ["//xcom/config:logback-xml"],
    test_class = "com.vaticle.typedb.example.xcom.XCOMTest",
    srcs = [
        "XCOMTest.java"
    ],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java/query",
        ":migration-lib",
        ":queries-lib"
    ],
    data = [
        "schema.tql"
    ],
    resources = ["//conf:logback-test.xml"],
    resource_strip_prefix = "conf",
    size = "large",
)

java_binary(
    name = "migration",
    classpath_resources = ["//xcom/config:logback-xml"],
    main_class = "com.vaticle.typedb.example.xcom.Migration",
    srcs = ["Migration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java/query",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = glob(["data/*.csv"]) + ["schema.tql"],
)

java_binary(
    name = "queries",
    classpath_resources = ["//xcom/config:logback-xml"],
    main_class = "com.vaticle.typedb.example.xcom.Queries",
    srcs = ["Queries.java", "TransactionMode.java", "ResearchTask.java", "InventoryItem.java", "Result.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java:typeql-lang",
    ],
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = glob(["data/*", "images/*", "*.md"]),
    license_type = "apache-header",
    size = "small",
)
