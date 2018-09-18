import grakn
import sys

'''
  To add a new query:
  1. add a question to the relevant variable with the _qs prefix
  2. add the function in 'execute_query_<new-query-number>` format
  3. write the implementation for the new function based on the execute_query_format(question)
'''

get_qs = []

aggregate_qs = []

compute_qs = []

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
