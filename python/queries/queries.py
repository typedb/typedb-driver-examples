import grakn
import sys

'''
  To add a new query:
  1. add a question to the relevant variable with the _qs prefix
  2. add the function in 'execute_query_<new-query-number>` format
  3. write the implementation for the new function based on the execute_query_format(question)
'''

get_qs = [
  "1. From 2018-09-10 onwards, which customers called the person with phone number +86 921 547 9004?",
  "2. who are the people aged under 20 who have received at least one phone call from a Cambridge customer aged above 50?"
  "3. Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176?",
  "4. Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?",
]

aggregate_qs = [
  "5. How does the average call duration among customers aged under 20 compare those aged above 40?",
]

compute_qs = [
  "6. ",
  "7. "
]

questions = get_qs + aggregate_qs + compute_qs

def print_to_log(title, content):
  print(title)
  print("")
  print(content)
  print("\n")

## The template for execute_query functions
# def execute_query_format(question)
#   print_to_log("Question: ", question)

#   query = [
#     "each line;",
#     "as an element;",
#     "ends with simocolon;",
#   ]
#   print_to_log("Query:", "\n".join(query))
#   query = "".join(query)

#   iterator = tx.query(query)
#   ## ... retrieve the answers
#   result = "example result"

#   print_to_log("Result:", result)


## From 2018-09-10 onwards, which customers called person with phone number +86 921 547 9004?
def execute_query_1(question):
  print_to_log("Question: ", question)

  query = [
    'match',
    '  $customer isa person has phone-number $phone-number;',
    '  $company isa company has name "Telecom";',
    '  (customer: $customer, provider: $company) isa contract;',
    '  $target isa person has phone-number "+86 921 547 9004";',
    '  (caller: $customer, callee: $target) isa call has started-at $started-at;',
    '  $min-date == 2018-09-14T17:18:49; $started-at > $min-date;',
    'get $phone-number;'
  ]

  print_to_log("Query:", "\n".join(query))
  query = "".join(query)

  iterator = tx.query(query)
  answers = iterator.collect_concepts()
  result = [ answer.value() for answer in answers ]

  print_to_log("Result:", result)

## who are the people aged under 20 who have received at least one phone call from a Cambridge customer aged above 60?
def execute_query_2(question):
  print_to_log("Question: ", question)

  query = [
    'match ',
    '  $person isa person has phone-number $phone-number has age < 20;'
    '  $customer isa person has city "London", has age > 60;',
    '  $company isa company has name "Telecom";',
    '  (customer: $customer, provider: $company) isa contract;',
    '  (caller: $customer, callee: $person) isa call;',
    'get $phone-number;'
  ]

  print_to_log("Query:", "\n".join(query))
  query = "".join(query)

  iterator = tx.query(query)
  answers = iterator.collect_concepts()
  result = [ answer.value() for answer in answers ]

  print_to_log("Result:", result)

## "Who are the common contacts of customers with phone numbers +7 171 898 0853 and +370 351 224 5176?
def execute_query_3(question):
  print_to_log("Question: ", question)

  query = [
    'match ',
    '  $common-contact isa person has phone-number $phone-number;',
    '  $customer-a isa person has phone-number "+7 171 898 0853";',
    '  $customer-b isa person has phone-number "+370 351 224 5176";',
    '  (caller: $customer-a, callee: $common-contact) isa call;',
    '  (caller: $customer-b, callee: $common-contact) isa call;',
    'get $phone-number;'
  ]
  print_to_log("Query:", "\n".join(query))
  query = "".join(query)

  iterator = tx.query(query)
  answers = iterator.collect_concepts()
  result = [ answer.value() for answer in answers ]

  print_to_log("Result:", result)

## Who are the customers who 1) have all called each other and 2) have all called person with phone number +48 894 777 5173 at least once?",
def execute_query_4(question):
  print_to_log("Question: ", question)

  query = [
    'match ',
    '  $target isa person has phone-number "+48 894 777 5173";',
    '  $company isa company has name "Telecom";',
    '  $customer-a isa person has phone-number $phone-number-a;',
    '  $customer-b isa person has phone-number $phone-number-b;',
    '  (customer: $customer-a, provider: $company) isa contract;',
    '  (customer: $customer-b, provider: $company) isa contract;',
    '  (caller: $customer-a, callee: $customer-b) isa call;',
    '  (caller: $customer-a, callee: $target) isa call;',
    '  (caller: $customer-b, callee: $target) isa call;',
    'get $phone-number-a, $phone-number-b;'
  ]
  print_to_log("Query:", "\n".join(query))
  query = "".join(query)

  iterator = tx.query(query)
  answers = iterator.collect_concepts()
  result = [ answer.value() for answer in answers ]

  print_to_log("Result:", result)

## How does the average call duration among customers aged under 20 compare those aged above 40?
def execute_query_5(question):
  print_to_log("Question: ", question)

  query_a = [
      'match',
      '  $customer isa person has age < 20;',
      '  $company isa company has name "Telecom";',
      '  (customer: $customer, provider: $company) isa contract;',
      '  (caller: $customer, callee: $anyone) isa call has duration $duration;',
      'aggregate mean $duration;'
  ]
  print_to_log("Query:", "\n".join(query_a))
  query_a = "".join(query_a)

  iterator_a = tx.query(query_a)
  result_a = next(iterator_a).number()
  result = ("Customers aged under 20 have made calls with average duration of " +
  str(round(result_a)) + " seconds.\n")

  query_b = [
    'match ' +
    '  $customer isa person has age > 40;',
    '  $company isa company has name "Telecom";',
    '  (customer: $customer, provider: $company) isa contract;',
    '  (caller: $customer, callee: $anyone) isa call has duration $duration;',
    'aggregate mean $duration;'
  ]
  print_to_log("Query:", "\n".join(query_b))
  query_b = "".join(query_b)

  iterator_b = tx.query(query_b)
  result_b = next(iterator_b).number()
  result += ("Customers aged above 40 have made calls with average duration of " +
    str(round(result_b)) + " seconds.\n")

  print_to_log("Result:", result)

def execute_query_all():
  for index, question in enumerate(questions):
    globals()["execute_query_" + str(index + 1)](question)
    print("\n - - -  - - -  - - -  - - - \n")

'''
  The code below:
  - gets user's selection wrt the queries to be executed
  - creates a Grakn client > session > transaction connected to the phone_calls keyspace
  - runs the right function based on the user's selection
  - closes the session
'''

## ask user which question to answer/query to execute
print("")
print("For which of these questions, on the phone_calls knowledge graph, do you want to execute the query?\n")
for  question in questions:
  print(question)
print("")

question_number = -1
while question_number < 0 or question_number > len(questions):
  question_number = int(input("choose a number (0 for to answer all questions): "))
print("")

## create a transaction to talk to the phone_calls keyspace
client = grakn.Grakn(uri = "localhost:48555")
session = client.session(keyspace = "phone_calls")
tx = session.transaction(grakn.TxType.READ);

## call the function to execute the query selected by user
execute_query_all() if question_number == 0 else locals()["execute_query_" + str(question_number)](questions[question_number - 1])

## we're done
session.close()
