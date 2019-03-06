#
# GRAKN.AI - THE KNOWLEDGE GRAPH
# Copyright (C) 2018 Grakn Labs Ltd
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

workspace(name = "graknlabs_examples")

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "io_bazel_rules_python",
    remote = "https://github.com/bazelbuild/rules_python.git",
    commit = "e6399b601e2f72f74e5aa635993d69166784dde1",
)
load("@io_bazel_rules_python//python:pip.bzl", "pip_repositories", "pip_import")
pip_repositories()

git_repository(
    name = "graknlabs_build_tools",
    remote = "https://github.com/graknlabs/build-tools.git",
    commit = "ded946f120c6194f82c7ed6b3f80d4d330e79ac3"
)

####################
# Load Build Tools #
####################

load("@graknlabs_build_tools//bazel:dependencies.bzl", "bazel_common", "bazel_deps",
     "bazel_toolchain", "bazel_rules_docker", "bazel_rules_nodejs", "bazel_rules_python")
bazel_common()
bazel_deps()
bazel_toolchain()
bazel_rules_docker()
bazel_rules_nodejs()
bazel_rules_python()

###########################
# Load local dependencies #
###########################

# for Java

load("//dependencies/maven:dependencies.bzl", maven_dependencies_for_build = "maven_dependencies")
maven_dependencies_for_build()

# for Node.js

load("@build_bazel_rules_nodejs//:package.bzl", "rules_nodejs_dependencies")
rules_nodejs_dependencies()

load("@build_bazel_rules_nodejs//:defs.bzl", "node_repositories", "npm_install")
node_repositories()

npm_install(
    name = "nodejs_dependencies",
    package_json = "//nodejs:package.json"
)

# for Python

load("@io_bazel_rules_python//python:pip.bzl", "pip_repositories", "pip_import")
pip_repositories()

pip_import(
    name = "phone_calls_migration_json_pypi_dependencies",
    requirements = "//python/migration/json:requirements.txt",
)
load("@phone_calls_migration_json_pypi_dependencies//:requirements.bzl", "pip_install")
pip_install()


################################
# Load Grakn Core dependencies #
################################

git_repository(
    name = "graknlabs_grakn_core",
    remote = "https://github.com/graknlabs/grakn",
    commit = '20750ca0a46b4bc252ad81edccdfd8d8b7c46caa' # grabl-marker: do not remove this comment, this is used for dependency-update by @graknlabs_grakn_core
)

load("@graknlabs_grakn_core//dependencies/maven:dependencies.bzl", maven_dependencies_for_grakn_core = "maven_dependencies")
maven_dependencies_for_grakn_core()


#######################################
# Load compiler dependencies for GRPC #
#######################################

load("@graknlabs_grakn_core//dependencies/compilers:dependencies.bzl", "grpc_dependencies")
grpc_dependencies()

load("@com_github_grpc_grpc//bazel:grpc_deps.bzl", com_github_grpc_grpc_bazel_grpc_deps = "grpc_deps")
com_github_grpc_grpc_bazel_grpc_deps()

load("@stackb_rules_proto//java:deps.bzl", "java_grpc_compile")
java_grpc_compile()


###########################
# Load Graql dependencies #
###########################

load("@graknlabs_grakn_core//dependencies/git:dependencies.bzl", "graknlabs_graql")
graknlabs_graql()

# Load ANTLR dependencies for Bazel
load("@graknlabs_graql//dependencies/compilers:dependencies.bzl", "antlr_dependencies")
antlr_dependencies()

# Load ANTLR dependencies for ANTLR programs
load("@rules_antlr//antlr:deps.bzl", "antlr_dependencies")
antlr_dependencies()

load("@graknlabs_graql//dependencies/maven:dependencies.bzl", graql_dependencies = "maven_dependencies")
graql_dependencies()


#################################
# Load Client Java dependencies #
#################################

git_repository(
    name = "graknlabs_client_java",
    remote = "https://github.com/graknlabs/client-java",
    commit = 'e2d3cba2216c5aadf58184c9abeb16dd3718c677' # grabl-marker: do not remove this comment, this is used for dependency-update by @graknlabs_client_java
)


###################################
# Load Client Python dependencies #
###################################

git_repository(
    name = "graknlabs_client_python",
    remote = "https://github.com/graknlabs/client-python",
    commit = 'bf27d7b0872ffadba15bd72db1716080875e7dd2' # grabl-marker: do not remove this comment, this is used for dependency-update by @graknlabs_client_python
)

# TODO: client python's pip_import should be called pypi_dependencies_grakn_client
pip_import(
    name = "pypi_dependencies",
    requirements = "@graknlabs_client_python//:requirements.txt",
)

load("@pypi_dependencies//:requirements.bzl", grakn_client_pip_install = "pip_install")
grakn_client_pip_install()


##################################
# Load Distribution Dependencies #
##################################

load("@graknlabs_build_tools//distribution:dependencies.bzl", "graknlabs_bazel_distribution")
graknlabs_bazel_distribution()

load("@graknlabs_bazel_distribution//github:dependencies.bzl", "github_dependencies_for_deployment")
github_dependencies_for_deployment()

load("@com_github_google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")
google_common_workspace_rules()

load("@graknlabs_grakn_core//dependencies/docker:dependencies.bzl", "docker_dependencies")
docker_dependencies()

pip_import(
    name = "pypi_deployment_dependencies",
    requirements = "@graknlabs_bazel_distribution//pip:requirements.txt",
)
load("@pypi_deployment_dependencies//:requirements.bzl", "pip_install")
pip_install()