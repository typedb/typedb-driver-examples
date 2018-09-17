# Copyright 2018 Grakn Labs Ltd
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import os
download_dir_name = "data-downloads"
routes_path = "{}/{}/routes/".format(os.path.dirname(__file__), download_dir_name)
timetables_path = "{}/{}/timetables/".format(os.path.dirname(__file__), download_dir_name)
migration_logs_path = "{}/src/migrations/logs/".format(os.path.dirname(__file__))

uri = 'http://localhost:4567'
keyspace = "tube_example"
