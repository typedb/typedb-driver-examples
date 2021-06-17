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

from typedb.client import TypeDB, TypeDBClient, SessionType, TransactionType
import json
import os
import re
from math import cos, asin, sqrt
import multiprocessing
import datetime

def entity_template(data):
    query = "insert $x isa " + data["type"]
    for attribute in data["attributes"]:
        query += ", has " + attribute["type"] + " " + str(attribute["value"])
    query += ";"
    return query


def relation_template(data):
    query = "match "
    for r, roleplayer in enumerate(data["roleplayers"]):
        query += "$" + str(r) + " has " + roleplayer["key_type"] + " " + str(roleplayer["key_value"]) + "; "

    # match the relation if required
    if "key_type" in data:
        query += "$x has " + data["key_type"] + " " + str(data["key_value"]) + "; "


    query += "insert $x ("
    for r, roleplayer in enumerate(data["roleplayers"]):
        query += roleplayer["role_name"] + ": $" + str(r)
        if r < len(data["roleplayers"]) - 1:
            query += ", "

    if "key_type" in data:
        query += ")"
    else:
        query += ") isa " + data["type"]

    if "attributes" in data:
        query += "; $x"
        for a, attribute in enumerate(data["attributes"]):
            query += " has " + attribute["type"] + " " + attribute["value"]
            if a < len(data["attributes"]) - 1:
                query += ","
    query += ";"
    return query


def string(value): return '"' + value + '"'


def unique_append(list, key, item):
    if item not in list[key]:
        list[key].append(item)

def zone_already_added(zone_name):
    zone_already_added = False
    for zone_query in relation_queries["zone"]:
        if 'isa zone; $x has name "' + zone_name + '"' in zone_query:
            zone_already_added = True
            break
    return zone_already_added

def get_distance_between_stations(data, from_station_id, to_station_id):
    """
        Looks up the stations with the given ids, finds their coordinates and
        calculates their distance using the Haversine formula

        :param data: in which the data for statons with the given ids exist
        :param from_station_id: naptan-id of the origin station
        :param to_station_id: naptan-id of the destination station
        :return: The as-th-crow-flies straight line distance between points 1 & 2
    """
    for station in data["stops"]:
        got_from_coordinates, got_to_coordinates = False, False
        if station["id"] == from_station_id:
            from_station_lat = station["lat"]
            from_station_lon = station["lon"]
            got_from_coordinates = True
        elif station["id"] == to_station_id:
            to_station_lat = station["lat"]
            to_station_lon = station["lon"]
            got_to_coordinates = True

        if got_from_coordinates and got_to_coordinates:
            break

    p = 0.017453292519943295
    a = 0.5 - cos((to_station_lat - from_station_lat) * p)/2 + cos(from_station_lat * p) * cos(to_station_lat * p) * (1 - cos((to_station_lon - from_station_lon) * p)) / 2
    return 12742 * asin(sqrt(a))

def construct_queries(entity_queries, relation_queries):
    timetable_files = os.listdir("datasets/tube-network/timetables/")

    for timetable_file in timetable_files:
        with open("datasets/tube-network/timetables/" + timetable_file) as template_file:
            data = json.load(template_file)

            unique_append(entity_queries, "tube-line",
                          entity_template(
                              {
                                  "type": "tube-line",
                                  "attributes": [
                                      {
                                          "type": "name",
                                          "value": string(data["lineName"])
                                      }
                                  ]
                              }
                          )
                          )

            for i, station in enumerate(data["stops"]):
                unique_append(entity_queries, "station",
                              entity_template(
                                  {
                                      "type": "station",
                                      "attributes": [
                                          {
                                              "type": "naptan-id",
                                              "value": string(station["id"])
                                          },
                                          {
                                              "type": "lon",
                                              "value": station["lon"]
                                          },
                                          {
                                              "type": "lat",
                                              "value": station["lat"]
                                          },
                                          {
                                              "type": "name",
                                              "value": string(station["name"])
                                          }
                                      ]
                                  }
                              )
                              )

                if "zone" in station:
                    zone_delimeter = re.compile(r'(\+|/)')
                    zones = [zone for zone in re.split(zone_delimeter, station["zone"]) if not zone_delimeter.match(zone)]

                    for zone in zones:
                        if zone_already_added(zone):
                            unique_append(relation_queries, "zone",
                                          relation_template(
                                              {
                                                  "type": "zone",
                                                  "key_type": "name",
                                                  "key_value": string(zone),
                                                  "roleplayers": [
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(station["id"]),
                                                          "role_name": "contained-station"
                                                      }
                                                  ]
                                              }
                                          )
                                          )
                        else:
                            unique_append(relation_queries, "zone",
                                          relation_template(
                                              {
                                                  "type": "zone",
                                                  "roleplayers": [
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(station["id"]),
                                                          "role_name": "contained-station"
                                                      }
                                                  ],
                                                  "attributes": [
                                                      {
                                                          "type": "name",
                                                          "value": string(zone)
                                                      }
                                                  ]
                                              }
                                          )
                                          )

                for r, route in enumerate(data['timetable']["routes"]):
                    route_identifier = timetable_file.split(".")[0] + "_" + str(r)
                    unique_append(relation_queries, "route",
                                  relation_template(
                                      {
                                          "type": "route",
                                          "roleplayers": [
                                              {
                                                  "type": "tube-line",
                                                  "key_type": "name",
                                                  "key_value": string(data["lineName"]),
                                                  "role_name": "route-operator"
                                              }
                                          ],
                                          "attributes": [
                                              {
                                                  "type": "identifier",
                                                  "value": string(route_identifier)
                                              }
                                          ]
                                      }
                                  )
                                  )

                    intervals = route["stationIntervals"][0]["intervals"] # first set of intervals is sufficient
                    if len(intervals) > 0 and intervals[0]["timeToArrival"] > 0:
                        intervals.insert(0, { "stopId": data["timetable"]["departureStopId"], "timeToArrival": 0 })
                    for i, interval in enumerate(intervals): # first station is the origin
                        if i == 0:
                            unique_append(relation_queries, "route",
                                          relation_template(
                                              {
                                                  "type": "route",
                                                  "key_type": "identifier",
                                                  "key_value": string(route_identifier),
                                                  "roleplayers": [
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(interval["stopId"]),
                                                          "role_name": "origin"
                                                      },
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(interval["stopId"]),
                                                          "role_name": "stop"
                                                      }
                                                  ]
                                              }
                                          )
                                          )
                        elif i == len(intervals) - 1: # last station is the destination
                            unique_append(relation_queries, "route",
                                          relation_template(
                                              {
                                                  "type": "route",
                                                  "key_type": "identifier",
                                                  "key_value": string(route_identifier),
                                                  "roleplayers": [
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(interval["stopId"]),
                                                          "role_name": "stop"
                                                      },
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(interval["stopId"]),
                                                          "role_name": "destination"
                                                      }
                                                  ]
                                              }
                                          )
                                          )
                        else: # any other station is an ordinary stop
                            unique_append(relation_queries, "route",
                                          relation_template(
                                              {
                                                  "type": "route",
                                                  "key_type": "identifier",
                                                  "key_value": string(route_identifier),
                                                  "roleplayers": [
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(interval["stopId"]),
                                                          "role_name": "stop"
                                                      }
                                                  ]
                                              }
                                          )
                                          )

                        if i < len(intervals) - 1: # there is no more stop after the last one
                            # last_time_to_arrival = 0
                            duration = intervals[i+1]["timeToArrival"] - interval["timeToArrival"]
                            route_section_identifier = timetable_file.split(".")[0] + "_route_section_" + str(i)
                            unique_append(entity_queries, "route-section",
                                          entity_template(
                                              {
                                                  "type": "route-section",
                                                  "attributes": [
                                                      {
                                                          "type": "duration",
                                                          "value": duration
                                                      },
                                                      {
                                                          "type": "identifier",
                                                          "value": string(route_section_identifier)
                                                      }
                                                  ]
                                              }
                                          )
                                          )
                            # last_time_to_arrival = interval["timeToArrival"]

                            unique_append(relation_queries, "route",
                                          relation_template(
                                              {
                                                  "type": "route",
                                                  "key_type": "identifier",
                                                  "key_value": string(route_identifier),
                                                  "roleplayers": [
                                                      {
                                                          "type": "route-section",
                                                          "key_type": "identifier",
                                                          "key_value": string(route_section_identifier),
                                                          "role_name": "section"
                                                      }
                                                  ]
                                              }
                                          )
                                          )

                            from_station_id = interval["stopId"]
                            to_station_id = intervals[i+1]["stopId"]
                            distance = get_distance_between_stations(data, from_station_id, to_station_id)

                            tunnel_identifier = from_station_id + "_tunnel_" + to_station_id
                            unique_append(relation_queries, "tunnel",
                                          relation_template(
                                              {
                                                  "type": "tunnel",
                                                  "roleplayers": [
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(from_station_id), # current stop
                                                          "role_name": "beginning"
                                                      },
                                                      {
                                                          "type": "station",
                                                          "key_type": "naptan-id",
                                                          "key_value": string(to_station_id), # next stop
                                                          "role_name": "end"
                                                      },
                                                      {
                                                          "type": "route-section",
                                                          "key_type": "identifier",
                                                          "key_value": string(route_section_identifier),
                                                          "role_name": "service"
                                                      }
                                                  ],
                                                  "attributes": [
                                                      {
                                                          "type": "identifier",
                                                          "value": string(tunnel_identifier)
                                                      },
                                                      {
                                                          "type": "distance",
                                                          "value": str(distance)
                                                      }
                                                  ]
                                              }
                                          )
                                          )


def insert(queries):
    with TypeDB.core_client("localhost:1729") as client:
        with client.session("tube_network", SessionType.DATA) as session:
            transaction = session.transaction(TransactionType.WRITE)
            for i, query in enumerate(queries):
                print(query)
                print("- - - - - - - - - - - - - -")
                transaction.query().insert(query)

                if i % 500 == 0:
                    transaction.commit()
                    transaction = session.transaction(TransactionType.WRITE)
            transaction.commit()


def insert_concurrently(queries, processes):
    cpu_count = multiprocessing.cpu_count()
    chunk_size = int(len(queries)/cpu_count)

    for i in range(cpu_count):
        if i == cpu_count - 1:
            chunk = queries[i*chunk_size:]
        else:
            chunk = queries[i*chunk_size:(i+1)*chunk_size]

        process = multiprocessing.Process(target=insert, args=(chunk,))
        process.start()
        processes.append(process)

    for process in processes:
        process.join()


entity_queries = {"tube-line": [], "station": [], "route-section": []}
relation_queries = {"zone": [], "route": [], "tunnel": []}


def init():
    start_time = datetime.datetime.now()

    construct_queries(entity_queries, relation_queries)

    entities, relations = [], []
    for k, v in entity_queries.items(): entities += v
    for k, v in relation_queries.items(): relations += v

    entity_processes = []
    relation_processes = []

    # insert_concurrently(entities, entity_processes)
    # insert_concurrently(relations, relation_processes)

    insert(entities)
    insert(relations)

    end_time = datetime.datetime.now()
    print("- - - - - -\nTime taken: " + str(end_time - start_time))
    print("\n" + str(len(entity_processes)) + " processes used to insert Entities.")
    print(str(len(relation_processes)) + " processes used to insert Relationship.")


if __name__ == "__main__":
    init()
