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

"""
We have the latitude and longitude of each station, so we can compute the as-the-crow-flies distance between connected
stops. We do this using the Haversine formula:

Haversine formula:	a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
c = 2 ⋅ atan2( √a, √(1−a) )
d = R ⋅ c

https://www.movable-type.co.uk/scripts/latlong.html

"""

from math import cos, asin, sqrt
import grakn
import tube_network_example.settings as settings
from utils.utils import check_response_length


def distance_from_coords(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """
    The Haversine formula to convert co-ordinates to as-the-crow-flies distance
    :param lat1: Latitude of point 1
    :param lon1: Longitude of point 1
    :param lat2: Latitude of point 2
    :param lon2: Longitude of point 2
    :return: The as-th-crow-flies straight line distance between points 1 & 2
    """
    p = 0.017453292519943295
    a = 0.5 - cos((lat2 - lat1) * p)/2 + cos(lat1 * p) * cos(lat2 * p) * (1 - cos((lon2 - lon1) * p)) / 2
    return 12742 * asin(sqrt(a))


if __name__ == "__main__":
    """
    Calculate the as-the-crow-flies distance between neighbouring stops, where the definition of neighbouring is that 
    they are connected by at least one tunnel  
    """
    client = grakn.Client(uri=settings.uri, keyspace=settings.keyspace)

    # Get all of the grid coordinates of the pairs of stations
    query = ("match\n"
             "$s1 isa station, has lon $lon1, has lat $lat1;\n"
             "$s2 isa station, has lon $lon2, has lat $lat2;\n"
             "($s1, $s2) isa neighbourship;\n"
             "get $s1, $s2, $lon1, $lat1, $lon2, $lat2;")
    print(query + "\n---\n")
    set_of_matches = client.execute(query)

    for s in set_of_matches:
        # Find the distance between the pair
        dist_km = distance_from_coords(float(s['lat1']['value']),
                                       float(s['lon1']['value']),
                                       float(s['lat2']['value']),
                                       float(s['lon2']['value']))
        # Insert that distance by adding it to the relationship
        query = ("match\n"
                 "$s1 isa station id {};\n"
                 "$s2 isa station id {};\n"
                 "$r($s1, $s2) isa neighbourship;\n"
                 "insert $r has distance {};").format(
            s['s1']['id'], s['s2']['id'], dist_km)
        print(query + "\n---\n")
        response = client.execute(query)
        check_response_length(response, min_length=1)
