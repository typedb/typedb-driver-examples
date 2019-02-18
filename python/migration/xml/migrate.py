## the Python client for Grakn
## https://github.com/graknlabs/grakn/tree/master/client-python
import grakn
## Python's built in module for dealing with .xml files.
## we will use it read data source files.
## https://medium.com/r/?url=https%3A%2F%2Fdocs.python.org%2F2%2Flibrary%2Fxml.etree.elementtree.html
import xml.etree.cElementTree as et

def build_phone_call_graph(inputs):
  '''
    gets the job done:
    1. creates a Grakn instance
    2. creates a session to the targeted keyspace
    3. loads the xml data to Grakn for each file
    4. closes the session
    :param input as list of dictionaties: each dictionary contains details required to parse the data
  '''
  client = grakn.Grakn(uri = "localhost:48555") # 1
  with client.session(keyspace = "phone_calls") as session: # 2 and 4
    for input in inputs:
      print("Loading from [" + input["data_path"] + "] into Grakn ...")
      load_data_into_grakn(input, session) # 3

def load_data_into_grakn(input, session):
  '''
    loads the xml data into our Grakn phone_calls keyspace:
    1. gets the data items as a list of dictionaries
    2. for each item dictionary
      a. creates a Grakn transaction
      b. constructs the corresponding Graql insert query
      c. runs the query
      d. commits the transaction
    :param input as dictionary: contains details required to parse the data
    :param session: off of which a transaction will be created
  '''
  items = parse_data_to_dictionaries(input) # 1

  for item in items: # 2
    with session.transaction(grakn.TxType.WRITE) as tx: # a
      graql_insert_query = input["template"](item) # b
      print("Executing Graql Query: " + graql_insert_query)
      tx.query(graql_insert_query) # c
      tx.commit() # d

  print("\nInserted " + str(len(items)) + " items from [ " + input["data_path"] + "] into Grakn.\n")

def company_template(company):
  return 'insert $company isa company, has name "' + company["name"] + '";'

def person_template(person):
  # insert person
  graql_insert_query = 'insert $person isa person, has phone-number "' + person["phone_number"] + '"'
  if "first_name" in person:
    # person is a customer
    graql_insert_query += ", has is-customer true"
    graql_insert_query += ', has first-name "' + person["first_name"] + '"'
    graql_insert_query += ', has last-name "' + person["last_name"] + '"'
    graql_insert_query += ', has city "' + person["city"] + '"'
    graql_insert_query += ", has age " + str(person["age"])
  else:
    # person is not a customer
    graql_insert_query += ", has is-customer false"
  graql_insert_query += ";"
  return graql_insert_query

def contract_template(contract):
  # match company
  graql_insert_query = 'match $company isa company, has name "' + contract["company_name"] + '";'
  # match person
  graql_insert_query += ' $customer isa person, has phone-number "' + contract["person_id"] + '";'
  # insert contract
  graql_insert_query += " insert (provider: $company, customer: $customer) isa contract;"
  return graql_insert_query

def call_template(call):
  # match caller
  graql_insert_query = 'match $caller isa person, has phone-number "' + call["caller_id"] + '";'
  # match callee
  graql_insert_query += ' $callee isa person, has phone-number "' + call["callee_id"] + '";'
  # insert call
  graql_insert_query += (" insert $call(caller: $caller, callee: $callee) isa call; " +
                         "$call has started-at " + call["started_at"] + "; " +
                         "$call has duration " + str(call["duration"]) + ";")
  return graql_insert_query

def parse_data_to_dictionaries(input):
  '''
    1. reads the file through a stream,
    2. adds the dictionary to the list of items
    :param input.data_path as string: the path to the data file, minus the format
    :returns items as list of dictionaries: each item representing a data item from the file at input.data_path
  '''
  items = []
  with open(input["data_path"] + ".xml", "rb") as inputfile:
    append = False
    for line in inputfile:
      if "<" + input["selector"] + ">" in str(line):
        ## start of a new xml tag
        buffer = line
        append = True
      elif "</" + input["selector"]  + ">" in str(line):
        ## we got a complete xml tag
        buffer += line
        append = False
        tnode = et.fromstring(buffer)
        ## parse the tag to a dictionary and append to tiems
        item = {}
        for element in tnode.getchildren():
          item[element.tag] = element.text
        items.append(item)
        ## delete the buffer to free the memory
        del buffer
      elif append:
        ## inside the current xml tag
        buffer += line
  return items

inputs = [
  {
    "data_path": "files/phone-calls/data/companies",
    "template": company_template,
    "selector": "company"
  },
  {
    "data_path": "files/phone-calls/data/people",
    "template": person_template,
    "selector": "person"
  },
  {
    "data_path": "files/phone-calls/data/contracts",
    "template": contract_template,
    "selector": "contract"
  },
  {
    "data_path": "files/phone-calls/data/calls",
    "template": call_template,
    "selector": "call"
  }
]

if __name__ == "__main__":
  build_phone_call_graph(inputs)
