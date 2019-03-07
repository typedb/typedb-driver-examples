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

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

def graknlabs_grakn_core():
    git_repository(
        name = "graknlabs_grakn_core",
        remote = "https://github.com/graknlabs/grakn",
        commit = '630247f81a39ce45dd822fc09c297ccef9a3b82f' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_grakn_core
    )

def graknlabs_client_java():
     git_repository(
         name = "graknlabs_client_java",
         remote = "https://github.com/graknlabs/client-java",
         commit = '0c565b9fa665c4167813856884becbb2b50c96d6' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_client_java
     )

def graknlabs_client_python():
     git_repository(
         name = "graknlabs_client_python",
         remote = "https://github.com/graknlabs/client-python",
         commit = 'bf27d7b0872ffadba15bd72db1716080875e7dd2' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_client_python
     )

def graknlabs_build_tools():
     git_repository(
         name = "graknlabs_build_tools",
         remote = "https://github.com/graknlabs/build-tools",
         commit = "b4d8600e093321bf8c81a5d1b70f34760a9bc036", # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_build_tools
     )




#def graknlabs_grakn_core():
#    git_repository(
#        name = "graknlabs_grakn_core",
#        remote = "https://github.com/graknlabs/grakn",
#        commit = '630247f81a39ce45dd822fc09c297ccef9a3b82f' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_grakn_core
#    )
#
#def graknlabs_client_java():
#     git_repository(
#         name = "graknlabs_client_java",
#         remote = "https://github.com/graknlabs/client-java",
#         commit = '3166c8f2c58f69336d1c02d604884a0b4a129a9e' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_client_java
#     )
#
#def graknlabs_client_python():
#     git_repository(
#         name = "graknlabs_client_python",
#         remote = "https://github.com/graknlabs/client-python",
#         commit = 'acffb3190ad6d720f9ebd5d8261bd8607c3e2d2b' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_client_python
#     )
#
#def graknlabs_build_tools():
#     git_repository(
#         name = "graknlabs_build_tools",
#         remote = "https://github.com/graknlabs/build-tools",
#         commit = "bd394a67896413dffdf50c2e66983483783d171d", # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_build_tools
#     )
