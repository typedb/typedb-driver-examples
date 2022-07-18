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
    name = "csv-migration-lib",
    srcs = ["CSVMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typeql//java/query",
        "@vaticle_typedb_client_java//api",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = [
        "//phone_calls:data-csv",
    ]
)

java_library(
    name = "json-migration-lib",
    srcs = ["JSONMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java/query",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson",
    ],
    data = [
        "//phone_calls:data-json",
    ]
)

java_library(
    name = "xml-migration-lib",
    srcs = ["XMLMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typeql//java/query",
        "@vaticle_typedb_client_java//api",
        "@maven//:org_sharegov_mjson",
        "@maven//:javax_xml_stream_stax_api"
    ],
    data = [
        "//phone_calls:data-xml",
    ]
)

java_library(
    name = "queries-lib",
    srcs = ["Queries.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typeql//java/query",
        "@vaticle_typedb_client_java//api",
    ]
)

java_test(
    name = "test",
    test_class = "com.vaticle.typedb.example.phoneCalls.PhoneCallsTest",
    srcs = [
        "PhoneCallsTest.java"
    ],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java/query",
        ":csv-migration-lib",
        ":json-migration-lib",
        ":xml-migration-lib",
        ":queries-lib"
    ],
    data = [
        "//phone_calls:schema.tql",
    ],
    resources = ["//conf:logback-test.xml"],
    resource_strip_prefix = "conf",
)

java_binary(
    name = "csv-migration",
    main_class = "com.vaticle.typedb.example.phoneCalls.CSVMigration",
    srcs = ["CSVMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java/query",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
    ],
    data = [
        "//phone_calls:data-csv",
    ]
)

java_binary(
    name = "json-migration",
    main_class = "com.vaticle.typedb.example.phoneCalls.JSONMigration",
    srcs = ["JSONMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java/query",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typeql//java:typeql-lang",
        "@maven//:org_sharegov_mjson",
        "@maven//:com_google_code_gson_gson",
    ],
    data = [
        "//phone_calls:data-json",
    ]
)

java_binary(
    name = "xml-migration",
    main_class = "com.vaticle.typedb.example.phoneCalls.XMLMigration",
    srcs = ["XMLMigration.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typeql//java/query",
        "@vaticle_typedb_client_java//api",
        "@maven//:org_sharegov_mjson",
        "@maven//:javax_xml_stream_stax_api"
    ],
    data = [
        "//phone_calls:data-xml",
    ]
)

java_binary(
    name = "queries",
    main_class = "com.vaticle.typedb.example.phoneCalls.Queries",
    srcs = ["Queries.java"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typeql//java/query",
        "@vaticle_typedb_client_java//api",
    ],
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = ["README.md"],
    license_type = "apache-header",
    size = "small",
)
