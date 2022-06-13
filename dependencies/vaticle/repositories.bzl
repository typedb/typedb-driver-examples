#
# Copyright (C) 2021 Vaticle
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

def vaticle_dependencies():
    #    git_repository(
    #        name = "vaticle_dependencies",
    #        remote = "https://github.com/vaticle/dependencies",
    #        commit = "465e60776ca3055ce85d90e94624d37db3f7e790", # sync-marker: do not remove this comment, this is used for sync-dependencies by @vaticle_dependencies
    #    )
    native.local_repository(
        name = "vaticle_dependencies",
        path = "../dependencies",
    )

def vaticle_typedb_common():
    git_repository(
        name = "vaticle_typedb_common",
        remote = "https://github.com/vaticle/typedb-common",
        commit = "d11cee9745e4559450ef4ccb140d4e9781587932",  # sync-marker: do not remove this comment, this is used for sync-dependencies by @vaticle_typedb_common
    )

def vaticle_typeql_lang_java():
    git_repository(
        name = "vaticle_typeql_lang_java",
        remote = "https://github.com/vaticle/typeql-lang-java",
        commit = "a46d4e6266d87cc20a6f8e45c36bdf97207aa65b",  # sync-marker: do not remove this comment, this is used for sync-dependencies by @vaticle_typeql_lang_java
    )

def vaticle_typedb_client_java():
    git_repository(
        name = "vaticle_typedb_client_java",
        remote = "https://github.com/vaticle/client-java",
        commit = "743c130a60414e78133f42c08e4d546bc73bc471",  # sync-marker: do not remove this comment, this is used for sync-dependencies by @vaticle_typedb_client_java
    )

def vaticle_typedb_client_python():
    git_repository(
        name = "vaticle_typedb_client_python",
        remote = "https://github.com/vaticle/typedb-client-python",
        commit = "b42231f4f516e1fe10099206a601d986a046d860",  # sync-marker: do not remove this comment, this is used for sync-dependencies by @vaticle_typedb_client_python
    )
