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

workspace(name = "vaticle_typedb_examples")

################################
# Load @vaticle_dependencies #
################################

load("//dependencies/vaticle:repositories.bzl", "vaticle_dependencies")
vaticle_dependencies()

# Load //builder/bazel for RBE
load("@vaticle_dependencies//builder/bazel:deps.bzl", "bazel_toolchain")
bazel_toolchain()

# Load //builder/java
load("@vaticle_dependencies//builder/java:deps.bzl", java_deps = "deps")
java_deps()

# Load //builder/kotlin
load("@vaticle_dependencies//builder/kotlin:deps.bzl", kotlin_deps = "deps")
kotlin_deps()
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories", "kt_register_toolchains")
kotlin_repositories()
kt_register_toolchains()

# Load //builder/python
load("@vaticle_dependencies//builder/python:deps.bzl", python_deps = "deps")
python_deps()
load("@rules_python//python:pip.bzl", "pip_install")

# Load //builder/nodejs
load("@vaticle_dependencies//builder/nodejs:deps.bzl", nodejs_deps = "deps")
nodejs_deps()
load("@build_bazel_rules_nodejs//:index.bzl", "node_repositories", "yarn_install")

# Load //builder/antlr
load("@vaticle_dependencies//builder/antlr:deps.bzl", antlr_deps = "deps", "antlr_version")
antlr_deps()

load("@rules_antlr//antlr:lang.bzl", "JAVA")
load("@rules_antlr//antlr:repositories.bzl", "rules_antlr_dependencies")
rules_antlr_dependencies(antlr_version, JAVA)

# Load //builder/grpc
load("@vaticle_dependencies//builder/grpc:deps.bzl", grpc_deps = "deps")
grpc_deps()
load("@com_github_grpc_grpc//bazel:grpc_deps.bzl",
com_github_grpc_grpc_deps = "grpc_deps")
com_github_grpc_grpc_deps()
load("@stackb_rules_proto//java:deps.bzl", "java_grpc_compile")
java_grpc_compile()
load("@stackb_rules_proto//node:deps.bzl", "node_grpc_compile")
node_grpc_compile()

# Load //tool/common
load("@vaticle_dependencies//tool/common:deps.bzl", "vaticle_dependencies_ci_pip",
    vaticle_dependencies_tool_maven_artifacts = "maven_artifacts")
vaticle_dependencies_ci_pip()

# Load //tool/checkstyle
load("@vaticle_dependencies//tool/checkstyle:deps.bzl", checkstyle_deps = "deps")
checkstyle_deps()

# Load //tool/unuseddeps
load("@vaticle_dependencies//tool/unuseddeps:deps.bzl", unuseddeps_deps = "deps")
unuseddeps_deps()

# Load //tool/sonarcloud
load("@vaticle_dependencies//tool/sonarcloud:deps.bzl", "sonarcloud_dependencies")
sonarcloud_dependencies()


######################################
# Load @vaticle_bazel_distribution #
######################################

load("@vaticle_dependencies//distribution:deps.bzl", "vaticle_bazel_distribution")
vaticle_bazel_distribution()

# Load //common
load("@vaticle_bazel_distribution//common:deps.bzl", "rules_pkg")
rules_pkg()
load("@rules_pkg//:deps.bzl", "rules_pkg_dependencies")
rules_pkg_dependencies()

# Load //pip
load("@vaticle_bazel_distribution//pip:deps.bzl", pip_deps = "deps")
pip_deps()

################################
# Load @vaticle dependencies #
################################

# Load repositories
load("//dependencies/vaticle:repositories.bzl",
    "vaticle_typeql", "vaticle_typedb_common", "vaticle_typedb_client_python", "vaticle_typedb_client_java")
vaticle_typeql()
vaticle_typedb_common()
vaticle_typedb_client_python()
vaticle_typedb_client_java()
load("@vaticle_typedb_client_java//dependencies/vaticle:repositories.bzl", "vaticle_typedb_protocol")
vaticle_typedb_protocol()
load("@vaticle_typedb_client_java//dependencies/vaticle:repositories.bzl", "vaticle_factory_tracing")
vaticle_factory_tracing()

# Load artifacts
load("//dependencies/vaticle:artifacts.bzl", "vaticle_typedb_artifact")
vaticle_typedb_artifact()

# Load maven artifacts
load("@vaticle_typedb_common//dependencies/maven:artifacts.bzl", vaticle_typedb_common_artifacts = "artifacts")
load("@vaticle_typeql//dependencies/maven:artifacts.bzl", vaticle_typeql_artifacts = "artifacts")
load("@vaticle_typedb_client_java//dependencies/maven:artifacts.bzl", vaticle_typedb_client_java_artifacts = "artifacts")
load("@vaticle_factory_tracing//dependencies/maven:artifacts.bzl", vaticle_factory_tracing_artifacts = "artifacts")

# Load pip dependencies
pip_install(
    name = "vaticle_typedb_client_python_pip",
    requirements = "@vaticle_typedb_client_python//:requirements.txt",
)

# Load python example dependencies
pip_install(
    name = "phone_calls_pip",
    requirements = "//telecom/phone_calls/python:requirements.txt"
)

pip_install(
    name = "sudoku_pip",
    requirements = "//games/sudoku:requirements.txt"
)

# Load java example dependencies
load("//dependencies/maven:artifacts.bzl", vaticle_typedb_examples_maven_artifacts = "artifacts")

# Load nodejs example dependencies
yarn_install(
    name = "npm",
    package_json = "//telecom/phone_calls/nodejs:package.json",
    yarn_lock = "//telecom/phone_calls/nodejs:yarn.lock"
)

############################
# Load @maven dependencies #
############################

load("@vaticle_dependencies//library/maven:rules.bzl", "maven")

maven(
    vaticle_dependencies_tool_maven_artifacts +
    vaticle_typedb_common_artifacts +
    vaticle_typeql_artifacts +
    vaticle_factory_tracing_artifacts +
    vaticle_typedb_client_java_artifacts +
    vaticle_typedb_examples_maven_artifacts,
)

###############################################
# Create @vaticle_typedb_examples_workspace_refs #
###############################################

load("@vaticle_bazel_distribution//common:rules.bzl", "workspace_refs")
workspace_refs(name = "vaticle_typedb_examples_workspace_refs")

load("@rules_jvm_external//:defs.bzl", "maven_install")
maven_install(
    name = "typedb-loader",
    artifacts = ["com.vaticle.typedb-osi:typedb-loader:1.4.0"],
    repositories = [
        "https://jitpack.io",
        "https://repo.vaticle.com/repository/maven/",
        "https://repo.vaticle.com/repository/osi-maven/",
        "https://repo1.maven.org/maven2",
    ],
    strict_visibility = True,
    version_conflict_policy = "pinned"
)

# Load JetBrains Compose for Kotlin.
load("@vaticle_dependencies//builder/compose:deps.bzl", compose_deps = "deps")
compose_deps()
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_binary", "kt_jvm_library")
