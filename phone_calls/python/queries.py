# coding=utf-8
from grakn.client import GraknClient
'''
  to add a new query implementation:
    1. add the question and function to the approriate list of dictionaries:
      current lists are: get_query_examples, aggregate_query_examples and compute_query_examples
      example:
        get_query_examples = [
          ...
          {
            "question": "new question?"
            "query_function": execute_query_#
          }
        ]
    2. add the function and its implementation:
      use the template below
'''


## The template for execute_query functions
# def execute_query_format(question, transaction)
#   print_to_log("Question: ", question)

#   ## queries are written as a list for better readibility
#   query = [
#     "each line;",
#     "as an element;",
#     "ends with simocolon;",
#   ]
#   ## join the query list elements with a new line before printing
#   print_to_log("Query:", "\n".join(query))
#   ## join the query list elements to obtain the quer as a string to be executed
#   query = "".join(query)

#   iterator = transaction.query(query)
#   ## ... retrieve the answers
#   result = "example result"

#   print_to_log("Result:", result)

def print_to_log(title, content):
    print(title)
    print("")
    print(content)
    print("\n")


""" From 2018-09-10 onwards, which customers called person with phone number +86 921 547 9004? """


def execute_query_1(question, transaction):
    print_to_log("Question: ", question)

    query = [
        'match',
        '  $customer isa person, has phone-number $phone-number;',
        '  $company isa company, has name "Telecom";',
        '  (customer: $customer, provider: $company) isa contract;',
        '  $target isa person, has phone-number "+86 921 547 9004";',
        '  (caller: $customer, callee: $target) isa call, has started-at $started-at;',
        '  $min-date == 2018-09-14T17:18:49; $started-at > $min-date;',
        'get $phone-number;'
    ]

    print_to_log("Query:", "\n".join(query))
    query = "".join(query)

    iterator = transaction.query(query)
    answers = iterator.collect_concepts()
    result = [answer.value() for answer in answers]

    print_to_log("Result:", result)

    return result


""" Who are the people who have received a call from a London customer aged over 50 who has previously called someone aged under 20? """


def execute_query_2(question, transaction):
    print_to_log("Question: ", question)

    query = [
        'match ',
        '  $suspect isa person, has city "London", has age > 50;',
        '  $company isa company, has name "Telecom";',
        '  (customer: $suspect, provider: $company) isa contract;',
        '  $pattern-callee isa person, has age < 20;',
        '  (caller: $suspect, callee: $pattern-callee) isa call, has started-at $pattern-call-date;',
        '  $target isa person, has phone-number $phone-number;',
        '  not { (customer: $target, provider: $company) isa contract; };',
        '  (caller: $suspect, callee: $target) isa call, has started-at $target-call-date;',
        '  $target-call-date > $pattern-call-date;',
        'get $phone-number;'
    ]

    print_to_log("Query:", "\n".join(query))
    query = "".join(query)

    iterator = transaction.query(query)
    answers = iterator.collect_concepts()
    result = [answer.value() for answer in answers]

    print_to_log("Result:", result)

    return result


""" Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176? """


def execute_query_3(question, transaction):
    print_to_log("Question: ", question)

    query = [
        'match ',
        '  $common-contact isa person, has phone-number $phone-number;',
        '  $customer-a isa person, has phone-number "+7 171 898 0853";',
        '  $customer-b isa person, has phone-number "+370 351 224 5176";',
        '  (caller: $customer-a, callee: $common-contact) isa call;',
        '  (caller: $customer-b, callee: $common-contact) isa call;',
        'get $phone-number;'
    ]

    print_to_log("Query:", "\n".join(query))
    query = "".join(query)

    iterator = transaction.query(query)
    answers = iterator.collect_concepts()
    result = [answer.value() for answer in answers]

    print_to_log("Result:", result)

    return result


""" Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?" """


def execute_query_4(question, transaction):
    print_to_log("Question: ", question)

    query = [
        'match ',
        '  $target isa person, has phone-number "+48 894 777 5173";',
        '  $company isa company, has name "Telecom";',
        '  $customer-a isa person, has phone-number $phone-number-a;',
        '  (customer: $customer-a, provider: $company) isa contract;',
        '  (caller: $customer-a, callee: $target) isa call;',
        '  $customer-b isa person, has phone-number $phone-number-b;',
        '  (customer: $customer-b, provider: $company) isa contract;',
        '  (caller: $customer-b, callee: $target) isa call;',
        '  (caller: $customer-a, callee: $customer-b) isa call;',
        'get $phone-number-a, $phone-number-b;'
    ]
    print_to_log("Query:", "\n".join(query))
    query = "".join(query)

    iterator = transaction.query(query)
    answers = iterator.collect_concepts()
    result = [answer.value() for answer in answers]

    print_to_log("Result:", result)

    return result


""" How does the average call duration among customers aged under 20 compare those aged over 40? """


def execute_query_5(question, transaction):
    print_to_log("Question: ", question)

    first_query = [
        'match',
        '  $customer isa person, has age < 20;',
        '  $company isa company, has name "Telecom";',
        '  (customer: $customer, provider: $company) isa contract;',
        '  (caller: $customer, callee: $anyone) isa call, has duration $duration;',
        'get $duration; mean $duration;'
    ]
    print_to_log("Query:", "\n".join(first_query))
    first_query = "".join(first_query)

    result = []
    first_answer = list(transaction.query(first_query))
    first_result = 0
    if len(first_answer) > 0:
        first_result = first_answer[0].number()
        result.append(first_result)

    output = ("Customers aged under 20 have made calls with average duration of " +
              str(round(first_result)) + " seconds.\n")

    second_query = [
        'match ' +
        '  $customer isa person, has age > 40;',
        '  $company isa company, has name "Telecom";',
        '  (customer: $customer, provider: $company) isa contract;',
        '  (caller: $customer, callee: $anyone) isa call, has duration $duration;',
        'get $duration; mean $duration;'
    ]
    print_to_log("Query:", "\n".join(second_query))
    second_query = "".join(second_query)

    second_answer = list(transaction.query(second_query))
    second_result = 0

    if len(second_answer) > 0:
        second_result = second_answer[0].number()
        result.append(second_result)

    output += ("Customers aged over 40 have made calls with average duration of " +
               str(round(second_result)) + " seconds.\n")

    print_to_log("Result:", output)

    return result


##
def execute_query_6(question, transaction):
    print_to_log("Question: ", question)


##
def execute_query_7(question, transaction):
    print_to_log("Question: ", question)


def execute_query_all(transaction):
    for query_example in query_examples:
        question = query_example["question"]
        query_function = query_example["query_function"]
        query_function(question, transaction)
        print("\n - - -  - - -  - - -  - - - \n")


get_query_examples = [
    {
        "question": "Since September 10th, which customers called the person with phone number +86 921 547 9004?",
        "query_function": execute_query_1
    },
    {
        "question": "Who are the people who have received a call from a London customer aged over 50 who has previously called someone aged under 20?",
        "query_function": execute_query_2
    },
    {
        "question": "Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176?",
        "query_function": execute_query_3
    },
    {
        "question": "Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?",
        "query_function": execute_query_4
    }
]

aggregate_query_examples = [
    {
        "question": "How does the average call duration among customers aged under 20 compare those aged over 40?",
        "query_function": execute_query_5
    }
]

query_examples = get_query_examples + aggregate_query_examples

def process_selection(qs_number, keyspace_name):
    ## create a transaction to talk to the phone_calls keyspace
    with GraknClient(uri="localhost:48555") as client:
        with client.session(keyspace=keyspace_name) as session:
            with session.transaction().read() as transaction:
                ## execute the query for the selected question
                if qs_number == 0:
                    execute_query_all(transaction)
                else:
                    question = query_examples[qs_number - 1]["question"]
                    query_function = query_examples[qs_number - 1]["query_function"]
                    query_function(question, transaction)


if __name__ == "__main__":

    '''
      The code below:
      - gets user's selection wrt the queries to be executed
      - creates a Grakn client > session > transaction connected to the phone_calls keyspace
      - runs the right function based on the user's selection
      - closes the session
    '''

    ## ask user which question to execute the query for
    print("")
    print("For which of these questions, on the phone_calls knowledge graph, do you want to execute the query?\n")
    for index, qs_func in enumerate(query_examples):
        print(str(index + 1) + ". " + qs_func["question"])
    print("")

    ## get user's question selection
    qs_number = -1
    while qs_number < 0 or qs_number > len(query_examples):
        qs_number = int(input("choose a number (0 for to answer all questions): "))
    print("")

    process_selection(qs_number, "phone_calls")