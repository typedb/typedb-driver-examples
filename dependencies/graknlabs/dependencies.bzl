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
        commit = '9f50793d83b5edeb8755e878cec7f28c8cb4ceb3' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_grakn_core
    )

def graknlabs_client_java():
     git_repository(
         name = "graknlabs_client_java",
         remote = "https://github.com/graknlabs/client-java",
         commit = '5d3503766954a1b7a0f26dc3dbd28c09f0ecf39f' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_client_java
     )

def graknlabs_client_python():
    git_repository(
        name = "graknlabs_client_python",
        remote = "https://github.com/graknlabs/client-python",
        commit = '1ce059329ce2af7e765cbd19a45a4e41f5411f20' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_client_python
    )

def graknlabs_client_nodejs():
    git_repository(
        name = "graknlabs_client_nodejs",
        remote = "https://github.com/graknlabs/client-nodejs",
        commit = '3d9e36b1563ac4278d9e87d4e897dd54a28f3b37' # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_client_nodejs
    )

def graknlabs_build_tools():
     git_repository(
         name = "graknlabs_build_tools",
         remote = "https://github.com/graknlabs/build-tools",
         commit = "d350513b965d01b094e936c368fba7454e9cd054", # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_build_tools
     )