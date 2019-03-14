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

################################
# Load Grakn Labs Dependencies #
################################
load("//dependencies/graknlabs:dependencies.bzl",
     "graknlabs_grakn_core", "graknlabs_client_java", "graknlabs_client_python", "graknlabs_build_tools")
graknlabs_grakn_core()
graknlabs_client_java()
graknlabs_client_python()
graknlabs_build_tools()

load("@graknlabs_grakn_core//dependencies/graknlabs:dependencies.bzl", "graknlabs_graql")
graknlabs_graql()

load("@graknlabs_build_tools//distribution:dependencies.bzl", "graknlabs_bazel_distribution")
graknlabs_bazel_distribution()


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

load("@io_bazel_rules_python//python:pip.bzl", "pip_repositories", "pip_import")
pip_repositories()

# Python dependencies for @graknlabs_build_tools and @graknlabs_bazel_distribution

pip_import(
    name = "graknlabs_build_tools_ci_pip",
    requirements = "@graknlabs_build_tools//ci:requirements.txt",
)
load("@graknlabs_build_tools_ci_pip//:requirements.bzl", graknlabs_build_tools_ci_pip_install = "pip_install")
graknlabs_build_tools_ci_pip_install()

pip_import(
    # TODO: bazel-distribution's pip_import should be called graknlabs_bazel_distribution_pip, set in client-python
    name = "pypi_deployment_dependencies",
    requirements = "@graknlabs_bazel_distribution//pip:requirements.txt",
)
load("@pypi_deployment_dependencies//:requirements.bzl", graknlabs_bazel_distribution_pip_install = "pip_install")
graknlabs_bazel_distribution_pip_install()


###########################
# Load Local Dependencies #
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
    package_json = "//phone_calls/nodejs:package.json"
)

# for Python

pip_import(
    name = "phone_calls_migration_json_pypi_dependencies",
    requirements = "//phone_calls/python:requirements.txt"
)
load("@phone_calls_migration_json_pypi_dependencies//:requirements.bzl", "pip_install")
pip_install()


#######################################
# Load Compiler Dependencies for GRPC #
#######################################

load("@graknlabs_build_tools//grpc:dependencies.bzl", "grpc_dependencies")
grpc_dependencies()

load("@com_github_grpc_grpc//bazel:grpc_deps.bzl", com_github_grpc_grpc_bazel_grpc_deps = "grpc_deps")
com_github_grpc_grpc_bazel_grpc_deps()

load("@stackb_rules_proto//java:deps.bzl", "java_grpc_compile")
java_grpc_compile()


################################
# Load Grakn Core Dependencies #
################################

load("@graknlabs_grakn_core//dependencies/maven:dependencies.bzl", grakn_core_dependencies = "maven_dependencies")
grakn_core_dependencies()

load("@graknlabs_build_tools//bazel:dependencies.bzl", "bazel_rules_docker")
bazel_rules_docker()


###########################
# Load Graql Dependencies #
###########################

# for Bazel
load("@graknlabs_graql//dependencies/compilers:dependencies.bzl", "antlr_dependencies")
antlr_dependencies()

# for ANTLR programs
load("@rules_antlr//antlr:deps.bzl", "antlr_dependencies")
antlr_dependencies()

load("@graknlabs_graql//dependencies/maven:dependencies.bzl", graql_dependencies = "maven_dependencies")
graql_dependencies()


###################################
# Load Client Python dependencies #
###################################

# TODO: client python's pip_import should be called pypi_dependencies_grakn_client
pip_import(
    name = "pypi_dependencies",
    requirements = "@graknlabs_client_python//:requirements.txt",
)

load("@pypi_dependencies//:requirements.bzl", graknlabs_client_python_pip_install = "pip_install")
graknlabs_client_python_pip_install()


#####################################
# Load Bazel common workspace rules #
#####################################

# TODO: Figure out why this cannot be loaded at earlier at the top of the file
load("@com_github_google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")
google_common_workspace_rules()