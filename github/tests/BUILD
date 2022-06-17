load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_test")

package(default_visibility = ["//github:__pkg__"])

kt_jvm_test(
    name = "test-migrator-explorer",
    srcs = glob(["*.kt"]),
    data = [
        "//github/datasets:github-data-json",
        "//github/schemas:github-data-schemas",
    ],
    tags = ["maven_coordinates=com.vaticle.typedb:typedb-studio-view:{pom_version}"],
    test_class = "github.tests.MigratorExplorerTest",
    deps = [
        "//github/state",
        "@maven//:junit_junit",
    ],
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
)
