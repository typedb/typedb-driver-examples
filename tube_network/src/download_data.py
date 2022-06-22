# Copyright 2021 Vaticle
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

import json
import ssl as ssl
from collections import namedtuple
from urllib.request import urlopen


def url_data_to_file(url, write_filename):
    '''
        Goes to the given URL and fetches the data found there, then stores it.
        :param url: URL to fetch data from
        :param write_filename: Location to store the data found
        :return:
    '''
    with urlopen(url, context=ssl.SSLContext()) as f:
        decoded_response = f.read().decode()
        with open(write_filename, "w") as output_file:
            output_file.write(decoded_response)


def dl_timetable_from_api(line, origin_naptan_id, direction, download_path):
    '''
        Downloads timetable data for a single tube line, from a specific origin stop, from the TFL API.
        :param line: Id of the tube line, as defined by TFL
        :param origin_naptan_id: The unique id of the station from which the train departs
        :param direction: TFL craziness for which way the train is travelling. Needed to retrieve the timetable data and
        not just the stops along the route
        :param download_path: Where to write the data once retrieved
        :return:
    '''
    # Example usage
    # dl_timetable_from_api("bakerloo", "940GZZLUHLT", "inbound", "downloads/bakerloo_timetable.json")

    # https://api.tfl.gov.uk/Line/Central/Timetable/940GZZLUHLT?direction=inbound
    url = "https://api.tfl.gov.uk/Line/{}/Timetable/{}?direction={}".format(line, origin_naptan_id, direction)
    write_filename = "{}{}_{}_{}.json".format(download_path, line, origin_naptan_id, direction)
    url_data_to_file(url, write_filename)


def dl_route_from_api(line, download_path):
    '''
        Downloads route data for a single tube line from the TFL API. This information contains the "routes" in my terminology,
        that run along the line. That is, not all lines are simple point-to-point. Some have multiple endpoints and even
        loops. This file defines those paths.
        :param line: The tube line id of interest
        :param download_path:
        :return:
    '''
    # Example usage
    # route_from_api("central", "downloads/central_routes.json")

    # https://api.tfl.gov.uk/line/central/route
    url = "https://api.tfl.gov.uk/line/{}/route".format(line)
    write_filename = "{}{}.json".format(download_path, line)
    url_data_to_file(url, write_filename)


def get_route_info(lines, load_path):
    '''
        Dissects the downloaded route information, extracting the parts required to fetch timetables that will describe all
        of the routes on the given lines.
        :param lines: Tube line ids as a list
        :param load_path: Directory where downloaded route information can be found.
        :return:
    '''
    all_route_info = []
    RouteInfo = namedtuple('RouteInfo', ['line', 'origin', 'direction'])
    for line in lines:
        s = "{}{}.json".format(load_path, line)
        with open(s, 'r') as f:
            data = json.load(f)
            for route_section in data['routeSections']:
                # We just want to retrieve all of the origins and the directions, since these are what we need to
                # request the timetables
                origin = route_section['originator']
                direction = route_section['direction']
                route_info = RouteInfo(line, origin, direction)
                all_route_info.append(route_info)

    return all_route_info


if __name__ == "__main__":

    print("\nDownloading data from TFL's website ...\n")

    lines = [
        "bakerloo",
        "central",
        "circle",
        "district",
        "hammersmith-city",
        "jubilee",
        "metropolitan",
        "northern",
        "piccadilly",
        "victoria",
        "waterloo-city"
    ]

    # Iterate over all of the above tube lines to retrieve the routes on that line
    for line in lines:
        dl_route_from_api(line, "tube_network/data/routes/")

    # Read the routes just downloaded, and look up the timetable information the origin station of each route. The only
    # way to retrieve a timetable is by specifying an origin.
    all_route_info = get_route_info(lines, "tube_network/data/routes/")
    for ri in all_route_info:
        dl_timetable_from_api(ri.line, ri.origin, ri.direction, "tube_network/data/timetables/")




