#
# GRAKN.AI - THE KNOWLEDGE GRAPH
# Copyright (C) 2019 Grakn Labs Ltd
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

load("@graknlabs_grakn_core//dependencies/graknlabs:dependencies.bzl", "graknlabs_graql", "graknlabs_protocol")
graknlabs_graql()
graknlabs_protocol()

load("@graknlabs_build_tools//distribution:dependencies.bzl", "graknlabs_bazel_distribution")
graknlabs_bazel_distribution()


###########################
# Load Bazel Dependencies #
###########################

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


#################################
# Load Build Tools Dependencies #
#################################

pip_import(
    name = "graknlabs_build_tools_ci_pip",
    requirements = "@graknlabs_build_tools//ci:requirements.txt",
)
load("@graknlabs_build_tools_ci_pip//:requirements.bzl",
graknlabs_build_tools_ci_pip_install = "pip_install")
graknlabs_build_tools_ci_pip_install()

pip_import(
    name = "graknlabs_bazel_distribution_pip",
    requirements = "@graknlabs_bazel_distribution//pip:requirements.txt",
)
load("@graknlabs_bazel_distribution_pip//:requirements.bzl",
graknlabs_bazel_distribution_pip_install = "pip_install")
graknlabs_bazel_distribution_pip_install()


###########################
# Load Local Dependencies #
###########################

# for Java

load("//dependencies/maven:dependencies.bzl", "maven_dependencies")
maven_dependencies()

# for Node.js

load("@build_bazel_rules_nodejs//:defs.bzl", "node_repositories", "yarn_install")
node_repositories()

yarn_install(
    name = "npm",
    package_json = "//phone_calls/nodejs:package.json",
    yarn_lock = "//phone_calls/nodejs:yarn.lock"
)

load("@npm//:install_bazel_dependencies.bzl", "install_bazel_dependencies")
install_bazel_dependencies()

# for Python

pip_import(
    name = "phone_calls_pip",
    requirements = "//phone_calls/python:requirements.txt"
)
load("@phone_calls_pip//:requirements.bzl",
phone_calls_pip_install = "pip_install")
phone_calls_pip_install()


##############################
# Load Protocol Dependencies #
##############################

load("@graknlabs_build_tools//grpc:dependencies.bzl", "grpc_dependencies")
grpc_dependencies()

load("@com_github_grpc_grpc//bazel:grpc_deps.bzl",
com_github_grpc_grpc_deps = "grpc_deps")
com_github_grpc_grpc_deps()

load("@stackb_rules_proto//java:deps.bzl", "java_grpc_compile")
java_grpc_compile()


################################
# Load Grakn Core Dependencies #
################################

load("@graknlabs_grakn_core//dependencies/graknlabs:dependencies.bzl",
"graknlabs_common", "graknlabs_console", "graknlabs_benchmark")
graknlabs_common()
graknlabs_console()
graknlabs_benchmark()

load("@graknlabs_grakn_core//dependencies/maven:dependencies.bzl",
graknlabs_grakn_core_maven_dependencies = "maven_dependencies")
graknlabs_grakn_core_maven_dependencies()

load("@graknlabs_benchmark//dependencies/maven:dependencies.bzl",
graknlabs_benchmark_maven_dependencies = "maven_dependencies")
graknlabs_benchmark_maven_dependencies()

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

load("@graknlabs_graql//dependencies/maven:dependencies.bzl",
graknlabs_graql_maven_dependencies = "maven_dependencies")
graknlabs_graql_maven_dependencies()


###################################
# Load Client Python Dependencies #
###################################

pip_import(
    name = "graknlabs_client_python_pip",
    requirements = "@graknlabs_client_python//:requirements.txt",
)

load("@graknlabs_client_python_pip//:requirements.bzl",
graknlabs_client_python_pip_install = "pip_install")
graknlabs_client_python_pip_install()


#####################################
# Load Bazel Common Workspace Rules #
#####################################

# TODO: Figure out why this cannot be loaded at earlier at the top of the file
load("@com_github_google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")
google_common_workspace_rules()