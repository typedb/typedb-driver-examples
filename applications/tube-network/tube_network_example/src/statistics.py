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

if __name__ == "__main__":

    client = grakn.Client(uri=settings.uri, keyspace=settings.keyspace)

    def perform_query(graql_string):
        """
        Just a wrapper function to print the graql query before sending it to the Grakn server.
        :param graql_string: query string
        :return: response from Grakn server
        """
        print("QUERY: {}".format(graql_string))
        # Send the graql query to the server
        response = client.execute(graql_string)
        return response

    # count
    # Find the number of stations, routes, tube lines, zones, etc.
    response = perform_query("compute count in station;")
    print(response)
    print("-----")

    # min
    response = perform_query("compute min of duration, in route-section;")
    print(response)
    print("-----")

    # min again
    response = perform_query("compute min of lat, in station;")
    print(response)
    print("-----")

    # max
    max_duration = perform_query("compute max of duration, in route-section;")
    print(max_duration)
    # Now we have the maximum duration we can query to find where in the graph this occurs
    match_query = ("match\n"
                   "$s1 isa station, has name $s1-name;\n"
                   "$s2 isa station, has name $s2-name;\n"
                   "$o isa station, has name $o-name;\n"
                   "$d isa station, has name $d-name;\n"
                   "$rs isa route-section, has duration {};\n"
                   "$t(beginning: $s1, end: $s2, service: $rs) isa tunnel;\n"
                   "$tl isa tube-line, has name $tl-name;"
                   "$r(section: $rs, origin: $o, destination: $d, route-operator: $tl) isa route;\n"
                   "get $s1-name, $s2-name, $o-name, $d-name, $tl-name;").format(max_duration)
    response = perform_query(match_query)

    print("-")
    print("Answer:")
    for m in response:
        print("Tunnel from {} to {}, via {} line, on the route going from {} to {}".format(
            m['s1-name']['value'], m['s2-name']['value'], m['tl-name']['value'], m['o-name']['value'],
            m['d-name']['value']))
    print("-----")

    # mean
    response = perform_query("compute mean of duration, in route-section;")
    print(response)
    print("-----")

    # median
    response = perform_query("compute median of duration, in route-section;")
    print(response)
    print("-----")

    # std
    response = perform_query("compute std of duration, in route-section;")
    print(response)
    print("-----")

    # sum
    # Find the sum duration of all of the route-sections that comprise a route. This query makes use of aggregation.
    query = (
        "match $s1 isa station has name \"Ealing Broadway Underground Station\"; $s2 isa station has name \"Upminster "
        "Underground Station\"; $rs isa route-section has duration $d; $r(origin: $s1, destination: $s2, "
        "section: $rs) isa route; aggregate sum $d;")
    print("QUERY: {}".format(query))
    route_duration = client.execute(query)
    print("Route duration: {}".format(route_duration))
    print("-----")
