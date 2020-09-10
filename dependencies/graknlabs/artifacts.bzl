#
# Copyright (C) 2020 Grakn Labs
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

load("@graknlabs_bazel_distribution//artifact:rules.bzl", "artifact_file")
load("@graknlabs_dependencies//distribution:deployment.bzl", "deployment_private")

def graknlabs_grakn_core_artifact():
    artifact_file(
        name = "graknlabs_grakn_core_artifact",
        group_name = "graknlabs_grakn_core",
        artifact_name = "grakn-core-server-linux-{version}.tar.gz",
        tag_source = deployment_private["artifact.release"],
        commit_source = deployment_private["artifact.snapshot"],
        commit = "da958b0632954626399264e03c647bd8da739f09",
    )
