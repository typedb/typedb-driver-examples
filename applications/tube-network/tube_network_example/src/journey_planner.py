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

def print_to_log(title, content):
  print(title)
  print("")
  print(content)
  print("\n")

if __name__ == "__main__":

    client = grakn.Grakn(uri=settings.uri)
    with client.session(keyspace=settings.keyspace) as session:

        # Get the departing station
        print("")
        valid_from_name = False
        while not valid_from_name:
            from_station_name = input("Enter the name of the station you're travelling from: ").title()

            print("Verifying station bane '" + from_station_name + "' ...")
            with session.transaction(grakn.TxType.READ) as transaction:
                from_station_list = list(transaction.query('match $sta isa station; { $sta has name "' + from_station_name + '"; } or { $sta has name "' + from_station_name + ' Underground Station"; }; get;'))
                if len(from_station_list) == 0:
                    print("No station with that name exists! Try again.")
                else:
                    from_station_id = from_station_list[0].map().get("sta").id
                    valid_from_name = True

        # Get the destination station
        print("")
        valid_to_name = False
        while not valid_to_name:
            to_station_name = input("Where to: ")
            print("Verifying station bane '" + from_station_name + "' ...")
            with session.transaction(grakn.TxType.READ) as transaction:
                to_station_list = list(transaction.query('match $sta isa station; { $sta has name "' + to_station_name + '"; } or { $sta has name "' + to_station_name + ' Underground Station"; }; get;'))
                if len(to_station_list) == 0:
                    print("No station with that name exists! Try again.")
                else:
                    to_station_id = to_station_list[0].map().get("sta").id
                    valid_to_name = True

        # Get the shortest path strategy
        print("")
        print("Shortest path strategies: ")
        print("1. Via fewest stops")
        print("2. Via fewest route changes")
        path_strategies = ["stops", "routes"]
        selected_path_strategy = -1
        while selected_path_strategy < 0 or selected_path_strategy > len(path_strategies) - 1:
            selected_path_strategy = int(input("Select a shortest path strategy: ")) - 1

        # Retrieve the shortest path
        print("")
        print(
            "Finding the shortest path between " + from_station_name +
            " to " + to_station_name +
            " via the fewest " + path_strategies[selected_path_strategy] + " ...")

        with session.transaction(grakn.TxType.READ) as transaction:
            compute_path_query = "compute path from " + from_station_id + ", to " + to_station_id

            if path_strategies[selected_path_strategy] == "stops":
                compute_path_query += ", in [station, tunnel];"
            elif path_strategies[selected_path_strategy] == "routes":
                compute_path_query += ", in [station, route];"

            shortest_path_concept_list = list(transaction.query(compute_path_query))
            if len(shortest_path_concept_list) == 0:
                print("No path found between the two stations!")
            else:
                shortest_path_concept_list = shortest_path_concept_list[0]

                # The response contains the different permutations for each path through stations. We are interested only in
                # the stations the path passes through
                shortest_path_stations = []
                for shortest_path_node_id in shortest_path_concept_list.list():
                    concepts_list= list(transaction.query("match $sta id " + shortest_path_node_id + "; $sta has name $nam; get;"))
                    if len(concepts_list) > 0:
                        concept = concepts_list[0]
                        if concept.map().get("sta").type().label() == 'station':
                            shortest_path_stations.append(concept.map().get("nam").value())

                print_to_log("Shortest path is: ", shortest_path_stations)