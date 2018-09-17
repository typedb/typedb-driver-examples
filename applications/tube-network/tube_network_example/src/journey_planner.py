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

import grakn
import tube_network_example.settings as settings
from utils.utils import match_get, get_match_id

if __name__ == "__main__":

    client = grakn.Client(uri=settings.uri, keyspace=settings.keyspace)

    # GET ME FROM:
    a_name = "Lancaster Gate Underground Station"
    # TO:
    b_name = "Queensbury Underground Station"

    # If you try b_name = "Upminster Underground Station"  # Tries to return 1048576 paths, which is 2^20, because most
    # stations are connected by 2 tunnels. We need a different return type from compute path before we can practically
    # query for this many paths
    # If you need to kill a query that is taking too long, you may need:
    # ./grakn server engine stop
    # ./grakn server engine start

    # Choose whether to find paths via the fewest stations, or via the fewest changes between routes
    FEWEST_STOPS = 0
    FEWEST_ROUTES = 1
    score_by = FEWEST_STOPS

    a_id = get_match_id(client.execute(match_get("$s1 isa station, has name \"{}\";".format(a_name))), "s1")
    b_id = get_match_id(client.execute(match_get("$s1 isa station, has name \"{}\";".format(b_name))), "s1")

    if score_by == FEWEST_STOPS:
        compute_query = "compute path from {}, to {}, in [station, tunnel];".format(a_id, b_id)  # Fewest stops
    elif score_by == FEWEST_ROUTES:
        compute_query = "compute path from {}, to {}, in [station, route];".format(a_id, b_id)  # Fewest changes
    else:
        raise ValueError("No method has been selected")
    print("Finding shortest paths...")
    shortest_paths = client.execute(compute_query)
    print("...done")

    # The response contains the different permutations for each path through stations. We are mainly interested in
    # which stations the path passes through
    station_paths = []
    for path in shortest_paths:
        station_ids = []
        for concept in path:
            if concept['type']['label'] == 'station':
                station_id = concept['id']
                station_ids.append(station_id)
        station_paths.append(station_ids)

    # Get the unique paths
    unique_paths = [list(x) for x in set(tuple(x) for x in station_paths)]

    # Get a printout of the names of the stations along the path:
    for i, unique_path in enumerate(unique_paths):
        station_names = []
        print("-- Option {} --".format(i))
        print("Via stations:")
        for station_id in unique_path:
            station_name = client.execute("match $s1 id {}, has name $n; get $n;".format(station_id))[0]['n']['value']
            print("+ " + station_name)
            station_names.append(station_name)
