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
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
# load specific verison of kotlin
http_archive(
    name = "io_bazel_rules_kotlin",
    sha256 = "34e8c0351764b71d78f76c8746e98063979ce08dcf1a91666f3f3bc2949a533d",
    url = "https://github.com/bazelbuild/rules_kotlin/releases/download/v1.9.5/rules_kotlin-v1.9.5.tar.gz",
)

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")
kotlin_repositories()
load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")
kt_register_toolchains()

# Load //builder/python
load("@vaticle_dependencies//builder/python:deps.bzl", python_deps = "deps")
python_deps()

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")
rules_jvm_external_deps()

# Load //builder/antlr
load("@vaticle_dependencies//builder/antlr:deps.bzl", antlr_deps = "deps", "antlr_version")
antlr_deps()

load("@rules_antlr//antlr:lang.bzl", "JAVA")
load("@rules_antlr//antlr:repositories.bzl", "rules_antlr_dependencies")
rules_antlr_dependencies(antlr_version, JAVA)

# Load //builder/proto_grpc
load("@vaticle_dependencies//builder/proto_grpc:deps.bzl", grpc_deps = "deps")
grpc_deps()

load("@rules_proto_grpc//:repositories.bzl", "rules_proto_grpc_repos", "rules_proto_grpc_toolchains")
rules_proto_grpc_toolchains()
rules_proto_grpc_repos()

load("@rules_proto_grpc//java:repositories.bzl", rules_proto_grpc_java_repos = "java_repos")
rules_proto_grpc_java_repos()

load("@io_grpc_grpc_java//:repositories.bzl", "IO_GRPC_GRPC_JAVA_ARTIFACTS")
load("@vaticle_dependencies//library/maven:rules.bzl", "parse_unversioned")
io_grpc_artifacts = [parse_unversioned(c) for c in IO_GRPC_GRPC_JAVA_ARTIFACTS]

# Load //builder/rust
load("@vaticle_dependencies//builder/rust:deps.bzl", rust_deps = "deps")
rust_deps()

load("@rules_rust//rust:repositories.bzl", "rules_rust_dependencies", "rust_register_toolchains", "rust_analyzer_toolchain_repository")
load("@rules_rust//tools/rust_analyzer:deps.bzl", "rust_analyzer_dependencies")
rules_rust_dependencies()
load("@rules_rust//rust:defs.bzl", "rust_common")
rust_register_toolchains(
    edition = "2021",
    extra_target_triples = [
        "aarch64-apple-darwin",
        "aarch64-unknown-linux-gnu",
        "x86_64-apple-darwin",
        "x86_64-pc-windows-msvc",
        "x86_64-unknown-linux-gnu",
    ],
    rust_analyzer_version = rust_common.default_version,
)

load("@vaticle_dependencies//library/crates:crates.bzl", "fetch_crates")
fetch_crates()
load("@crates//:defs.bzl", "crate_repositories")
crate_repositories()

load("@vaticle_dependencies//tool/swig:deps.bzl", swig_deps = "deps")
swig_deps()

# Load //tool/common
load("@vaticle_dependencies//tool/common:deps.bzl", "vaticle_dependencies_ci_pip",
    vaticle_dependencies_tool_maven_artifacts = "maven_artifacts")
vaticle_dependencies_ci_pip()
load("@vaticle_dependencies_ci_pip//:requirements.bzl", "install_deps")
install_deps()

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

# Load //github
load("@vaticle_bazel_distribution//github:deps.bzl", github_deps = "deps")
github_deps()

# Load //pip
load("@vaticle_bazel_distribution//pip:deps.bzl", pip_deps = "deps")
pip_deps()
load("@vaticle_bazel_distribution_pip//:requirements.bzl", install_pip_deps = "install_deps")
install_pip_deps()

# Load //docs
load("@vaticle_bazel_distribution//docs:python/deps.bzl", docs_deps = "deps")
docs_deps()
load("@vaticle_dependencies_tool_docs//:requirements.bzl", install_doc_deps = "install_deps")
install_doc_deps()

load("@vaticle_bazel_distribution//docs:java/deps.bzl", java_doc_deps = "deps")
java_doc_deps()
load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")
google_common_workspace_rules()

################################
# Load @vaticle dependencies #
################################

# Load repositories
load("//dependencies/vaticle:repositories.bzl", "vaticle_typedb_driver")
vaticle_typedb_driver()

load("@vaticle_typedb_driver//dependencies/vaticle:repositories.bzl", "vaticle_typedb_protocol", "vaticle_typeql")
vaticle_typeql()
vaticle_typedb_protocol()

# Load artifacts
load("@vaticle_typedb_driver//dependencies/vaticle:artifacts.bzl", "vaticle_typedb_artifact")
vaticle_typedb_artifact()

# Load maven artifacts
load("@vaticle_typeql//dependencies/maven:artifacts.bzl", vaticle_typeql_artifacts = "artifacts")
load("@vaticle_typedb_driver//dependencies/maven:artifacts.bzl", vaticle_typedb_driver_artifacts = "artifacts")
load("@vaticle_typedb_driver//dependencies/vaticle:artifacts.bzl", vaticle_typedb_vaticle_maven_artifacts = "maven_artifacts")

# Load python toolchains
load("@vaticle_typedb_driver//python:python_versions.bzl", "register_all_toolchains")
register_all_toolchains()

# Load pip dependencies
load("@rules_python//python:pip.bzl", "pip_parse")
pip_parse(
    name = "vaticle_typedb_driver_python_pip",
    requirements = "@vaticle_typedb_driver//python:requirements.txt",
)
load("@vaticle_typedb_driver_python_pip//:requirements.bzl", "install_deps")
install_deps()

# Load python example dependencies
pip_parse(
    name = "phone_calls_pip",
    requirements = "//telecom/phone_calls/python:requirements.txt"
)
load("@phone_calls_pip//:requirements.bzl", "install_deps")
install_deps()

# Load java example dependencies
load("//dependencies/maven:artifacts.bzl", vaticle_typedb_examples_maven_artifacts = "artifacts")

# Load nodejs example dependencies

# Load //builder/nodejs directly to get new versions
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
http_archive(
    name = "aspect_rules_js",
    sha256 = "2cfb3875e1231cefd3fada6774f2c0c5a99db0070e0e48ea398acbff7c6c765b",
    strip_prefix = "rules_js-1.42.3",
    url = "https://github.com/aspect-build/rules_js/releases/download/v1.42.3/rules_js-v1.42.3.tar.gz",
)

http_archive(
    name = "aspect_rules_ts",
    sha256 = "4c3f34fff9f96ffc9c26635d8235a32a23a6797324486c7d23c1dfa477e8b451",
    strip_prefix = "rules_ts-1.4.5",
    url = "https://github.com/aspect-build/rules_ts/releases/download/v1.4.5/rules_ts-v1.4.5.tar.gz",
)

http_archive(
    name = "aspect_rules_jasmine",
    sha256 = "58f4981cd8972225bce38dcacdd897ae5ac8b41cf5968363d47b939f6c745802",
    strip_prefix = "rules_jasmine-1.2.0",
    url = "https://github.com/aspect-build/rules_jasmine/releases/download/v1.2.0/rules_jasmine-v1.2.0.tar.gz",
)

load("@aspect_rules_js//js:repositories.bzl", "rules_js_dependencies")
rules_js_dependencies()

load("@aspect_rules_jasmine//jasmine:dependencies.bzl", "rules_jasmine_dependencies")
rules_jasmine_dependencies()

load("@rules_nodejs//nodejs:repositories.bzl", "DEFAULT_NODE_VERSION", "nodejs_register_toolchains")
nodejs_register_toolchains(
    name = "nodejs",
    node_version = DEFAULT_NODE_VERSION,
)

load("@aspect_rules_js//npm:repositories.bzl", "npm_translate_lock")

# Validate that we can use a yarn.lock file in place of pnpm-lock.yaml
npm_translate_lock(
    name = "npm",
    data = ["//telecom/phone_calls/nodejs:package.json"],
    pnpm_lock = "//telecom/phone_calls/nodejs:pnpm-lock.yaml",
    update_pnpm_lock = True,
    yarn_lock = "//telecom/phone_calls/nodejs:yarn.lock",
)

load("@npm//:repositories.bzl", "npm_repositories")

npm_repositories()

############################
# Load @maven dependencies #
############################

load("@vaticle_dependencies//library/maven:rules.bzl", "maven")
maven(
    vaticle_typedb_examples_maven_artifacts +
    vaticle_typeql_artifacts +
    vaticle_typedb_driver_artifacts +
    vaticle_dependencies_tool_maven_artifacts +
    io_grpc_artifacts,
    generate_compat_repositories = True,
    internal_artifacts = vaticle_typedb_vaticle_maven_artifacts,
)

load("@maven//:compat.bzl", "compat_repositories")
compat_repositories()

###############################################
# Create @vaticle_typedb_examples_workspace_refs #
###############################################

load("@vaticle_bazel_distribution//common:rules.bzl", "workspace_refs")
workspace_refs(name = "vaticle_typedb_examples_workspace_refs")

load("@rules_jvm_external//:defs.bzl", "maven_install")
maven_install(
    name = "typedb-loader",
    artifacts = ["com.vaticle.typedb-osi:typedb-loader:1.9.1"],
    repositories = [
        "https://jitpack.io",
        "https://repo1.maven.org/maven2",
	    "https://repo.typedb.com/public/osi/maven/",
        "https://repo.typedb.com/public/public-release/maven/",
    	"https://repo.typedb.com/public/public-snapshot/maven/"
    ],
    strict_visibility = True,
    version_conflict_policy = "pinned"
)
