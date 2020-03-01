# Copyright 2020 Grakn Labs
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

from grakn.client import GraknClient


def print_to_log(title, content):
  print(title)
  print("")
  print(content)
  print("\n")


def find_path(session, from_id, to_id, strategy):
    with session.transaction().read() as transaction:
        compute_path_query = "compute path from " + from_id + ", to " + to_id

        if strategy == "stops":
            compute_path_query += ", in [station, tunnel];"
        elif strategy == "routes":
            compute_path_query += ", in [station, route];"

        shortest_path_concept_list = list(transaction.query(compute_path_query))

        if len(shortest_path_concept_list) == 0:
            print("No path found between the two stations!")
        else:
            shortest_path_concept_list = shortest_path_concept_list[0]

            # The response contains the different permutations for each path through stations. We are interested
            # only in the stations the path passes through
            shortest_path_stations = []
            for shortest_path_node_id in shortest_path_concept_list.list():
                concepts_list = list(transaction.query("match $sta id " +
                                                       shortest_path_node_id + "; $sta has name $nam; get;"))
                if len(concepts_list) > 0:
                    concept = concepts_list[0]
                    if concept.map().get("sta").type().label() == 'station':
                        shortest_path_stations.append(concept.map().get("nam").value())

            print_to_log("Shortest path is: ", shortest_path_stations)

    return shortest_path_stations


def get_station_by_name(session, station_name):
    with session.transaction().read() as transaction:
        print('match $sta isa station; { $sta has name "' + station_name.title() +
              '"; } or { $sta has name "' + station_name.title() +
              ' Underground Station"; }; get;')
        station_list = list(transaction.query('match $sta isa station; { $sta has name "' + station_name.title() +
                                              '"; } or { $sta has name "' + station_name.title() +
                                              ' Underground Station"; }; get;'))

        if len(station_list) == 0:
            return None
        else:
            return station_list[0].map().get("sta").id

def init(from_station_name, to_station_name, selected_path_strategy):
    with GraknClient(uri="localhost:48555") as client:
        with client.session(keyspace="tube_network") as session:
            # Get the departing station
            valid_from_name = False
            while not valid_from_name:
                if from_station_name is None:
                    from_station_name = input("Enter the name of the station you're travelling from: ").title()
                print("Verifying station name '" + from_station_name + "' ...\n")
                from_station_id = get_station_by_name(session, from_station_name)
                print(from_station_id)

                if from_station_id:
                    valid_from_name = True
                else:
                    print("No station with that name exists! Try again.\n")

            # Get the destination station
            valid_to_name = False
            while not valid_to_name:
                if to_station_name is None:
                    to_station_name = input("Where to: ")
                print("Verifying station name '" + to_station_name + "' ...\n")
                to_station_id = get_station_by_name(session, to_station_name)

                if to_station_id:
                    valid_to_name = True
                else:
                    print("No station with that name exists! Try again.\n")

            # Get the shortest path strategy
            print("Shortest path strategies: ")
            print("1. Via fewest stops")
            print("2. Via fewest route changes")
            path_strategies = ["stops", "routes"]

            if selected_path_strategy is None:
                selected_path_strategy = -1

            while selected_path_strategy < 0 or selected_path_strategy > len(path_strategies) - 1:
                selected_path_strategy = int(input("Select a shortest path strategy: ")) - 1
                print("")

            # Retrieve the shortest path
            print(
                    "Finding the shortest path between " + from_station_name +
                    " to " + to_station_name +
                    " via the fewest " + path_strategies[selected_path_strategy] + " ...")

            find_path(session, from_station_id, to_station_id, path_strategies[selected_path_strategy])

if __name__ == "__main__":
    init(None, None, None)
