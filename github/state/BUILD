load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

package(default_visibility = ["//github:__subpackages__"])

kt_jvm_library(
    name = "state",
    srcs = glob(["*.kt"]),
    tags = ["maven_coordinates=com.vaticle.typedb:typedb-studio-view:{pom_version}"],
    deps = [
        "@vaticle_typedb_client_java//:client-java",
        "@vaticle_typedb_client_java//api",
        "@vaticle_typedb_common//:common",
        "@vaticle_typeql_lang_java//:typeql-lang",
        "@vaticle_typeql_lang_java//common",
        "@vaticle_typeql_lang_java//query",
        # External Maven Dependencies
        "@maven//:org_kohsuke_github_api",
        "@maven//:com_eclipsesource_minimal_json_minimal_json",
        "@maven//:org_jetbrains_compose_runtime_runtime_desktop",
    ],
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
)
