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

from typedb.client import TypeDB, SessionType, TransactionType

def print_to_log(title, content):
  print(title)
  print("")
  print(content)
  print("\n")


# How many stations do exist?
def query_station_count(question, transaction):
    print_to_log("Question: ", question)

    query = 'match $x isa station; count;'

    print_to_log("Query:", query)

    answer = transaction.query().match_aggregate(query)
    number_of_stations = answer.get().as_int()

    print("Number of stations: " + str(number_of_stations))

    return number_of_stations


# How long is the shortest trip between two stations?
def query_shortest_trip(question, transaction):
    print_to_log("Question: ", question)

    query = 'compute min of duration, in route-section;'

    print_to_log("Query:", query)

    answer = list(transaction.query(query))[0]
    min_duration = answer.number()

    print("Shortest Trip: " + str(min_duration))

    return min_duration


# Which is the west most station in London?
def query_northernmost_station(question, transaction):
    print_to_log("Question: ", question)

    query = 'compute min of lat, in station;'

    print_to_log("Query:", query)

    answer = list(transaction.query(query))[0]
    lat = answer.number()

    query = [
        'match',
        '   $sta isa station, has lat $lat, has name $nam;',
        '   $lat ' + str(lat) + ';',
        'get $nam;'
    ]

    print_to_log("Query:", "\n".join(query))
    query = "".join(query)

    answers = [ans.get("nam") for ans in transaction.query(query)]
    result = [answer.get_value() for answer in answers]

    print_to_log("Northmost stations with " + str(lat) + " are: ", result)

    return [lat, result]


# How long is the longest trip between two stations?
def query_longest_trip(question, transaction):
    print_to_log("Question: ", question)

    query = 'compute max of duration, in route-section;'

    print_to_log("Query:", query)

    answer = list(transaction.query(query))[0]
    max_duration = answer.number()
    print(max_duration)

    query = [
        'match',
        '   $rou (section: $sec, origin: $ori, destination: $des, route-operator: $tul) isa route;',
        '   $sec isa route-section, has duration ' + str(max_duration) + ';',
        '   $tul isa tube-line, has name $tul-nam;',
        '   $tun (beginning: $sta1, end: $sta2, service: $sec) isa tunnel;',
        '   $sta1 isa station, has name $sta1-nam;',
        '   $sta2 isa station, has name $sta2-nam;',
        '   $ori isa station, has name $ori-nam;',
        '   $des isa station, has name $des-nam;',
        'get;'
    ]

    print_to_log("Query:", "\n".join(query))
    query = "".join(query)

    answers = transaction.query(query)
    result = []
    for answer in answers:
        answer = answer.map()
        print_to_log("Longest trip is found in: ", "Tunnel from " +
                     answer.get("sta1-nam").get_value() +
                     " to " + answer.get("sta2-nam").get_value() +
                     ", via " + answer.get("tul-nam").get_value() +
                     ", on the route going from " +
                     answer.get("ori-nam").get_value() +
                     " to " + answer.get("des-nam").get_value())

        result.append([
            answer.get("sta1-nam").get_value(),
            answer.get("sta2-nam").get_value(),
            answer.get("tul-nam").get_value(),
            answer.get("ori-nam").get_value(),
            answer.get("des-nam").get_value()
        ])

    return [max_duration, result]

# What's the average duration of all trips?
def query_avg_duration(question, transaction):
    print_to_log("Question: ", question)

    query = 'compute mean of duration, in route-section;'

    print_to_log("Query:", query)

    answer = list(transaction.query(query))[0]
    mean_duration = answer.number()

    print("Average duration: " + str(mean_duration))

    return mean_duration


# What's the median duration among all trips?
def query_median_duration(question, transaction):
    print_to_log("Question: ", question)

    query = 'compute median of duration, in route-section;'

    print_to_log("Query:", query)

    answer = list(transaction.query(query))[0]
    median_duration = answer.number()

    print("Median of durations: " + str(median_duration))

    return median_duration


# What's the standard deviation of trip durations?
def query_std_duration(question, transaction):
    print_to_log("Question: ", question)

    query = 'compute std of duration, in route-section;'

    print_to_log("Query:", query)

    answer = list(transaction.query(query))[0]
    std_duration = answer.number()

    print("Standard deviation of durations: " + str(std_duration))

    return std_duration


def execute_query_all(transaction):
  for qs_func in query_examples:
    question = qs_func["question"]
    query_function = qs_func["query_function"]
    query_function(question, transaction)
    print("\n - - -  - - -  - - -  - - - \n")


query_examples = [
    {
        "question": "How many stations do exist?",
        "query_function": query_station_count
    },
    # TODO(vmax): enable when compute queries are supported again
    # {
    #     "question": "How long is the shortest trip between two stations?",
    #     "query_function": query_shortest_trip
    # },
    # {
    #     "question": "Which is the northernmost station in London?",
    #     "query_function": query_northernmost_station
    # },
    # {
    #     "question": "How long is the longest trip between two stations?",
    #     "query_function": query_longest_trip
    # },
    # {
    #     "question": "What's the average duration of all trips?",
    #     "query_function": query_avg_duration
    # },
    # {
    #     "question": "What's the median duration among all trips?",
    #     "query_function": query_median_duration
    # },
    # {
    #     "question": "What's the standard deviation of trip durations?",
    #     "query_function": query_std_duration
    # }
]

def init(qs_number):
    # create a transaction to talk to the database
    with TypeDB.core_client("localhost:1729") as client:
        with client.session("tube_network", SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                # execute the query for the selected question
                if qs_number == 0:
                    execute_query_all(transaction)
                else:
                    question = query_examples[qs_number - 1]["question"]
                    query_function = query_examples[qs_number - 1]["query_function"]
                    query_function(question, transaction)

if __name__ == "__main__":

    """
        The code below:
        - gets user's selection wrt the queries to be executed
        - creates a TypeDB client > session > transaction connected to the database
        - runs the right function based on the user's selection
        - closes the session and transaction
    """

    # ask user which question to execute the query for
    print("")
    print("For which of these questions, on the tube knowledge graph, do you want to execute the query?\n")
    for index, qs_func in enumerate(query_examples):
        print(str(index + 1) + ". " + qs_func["question"])
    print("")

    # get user's question selection
    qs_number = -1
    while qs_number < 0 or qs_number > len(query_examples):
        qs_number = int(input("choose a number (0 for to answer all questions): "))
    print("")

    init(qs_number)
