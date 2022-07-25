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

java_binary(
    name = "loader",
    main_class = "com.vaticle.typedb.example.biology.catalogueOfLife.Loader",
    srcs = ["Loader.java"],
    deps = [
        "@maven//:com_univocity_univocity_parsers",
        "@maven//:info_picocli_picocli",
        "@typedb-loader//:com_vaticle_typedb_osi_typedb_loader",
    ],
    resources = [
        ":schema.tql",
        ":loader-config.json.template",
    ],
)

java_test(
    name = "tests",
    test_class = "com.vaticle.typedb.example.biology.catalogueOfLife.test.CoLTest",
    srcs = [
        "test/CoLTest.java"
    ],
    deps = [
        ":loader",
        "@maven//:com_univocity_univocity_parsers",
        "@maven//:info_picocli_picocli",
        "@typedb-loader//:com_vaticle_typedb_osi_typedb_loader",
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java:typeql-lang",
    ],
    data = glob(["test/data/*.tsv"]),
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["**/*.java", "**/*.tql", "BUILD"]),
    exclude = glob(["test/data/*"]) + ["loader-config.json.template"],
    license_type = "apache-header",
    size = "small",
)
