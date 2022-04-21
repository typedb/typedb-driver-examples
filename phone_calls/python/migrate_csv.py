# the Python client for TypeDB
# https://github.com/vaticle/client-python
# Python's built in module for dealing with .csv files.
# we will use it read data source files.
# https://docs.python.org/3/library/csv.html#dialects-and-formatting-parameters
import csv
from typedb.client import TypeDB, SessionType, TransactionType


def build_phone_call_graph(inputs, data_path, keyspace_name):
    """
      gets the job done:
      1. creates a TypeDB instance
      2. creates a session to the targeted keyspace
      3. for each input:
        - a. constructs the full path to the data file
        - b. loads csv to TypeDB
      :param input as list of dictionaties: each dictionary contains details required to parse the data
    """
    with TypeDB.core_client("localhost:1729") as client:  # 1
        with client.session(keyspace_name, SessionType.DATA) as session:  # 2
            for input in inputs:
                input["file"] = input["file"].replace(data_path, "")  # for testing purposes
                input["file"] = data_path + input["file"]  # 3a
                print("Loading from [" + input["file"] + ".csv] into TypeDB ...")
                load_data_into_typedb(input, session)  # 3b


def load_data_into_typedb(input, session):
    """
      loads the csv data into our TypeDB phone_calls keyspace:
      1. gets the data items as a list of dictionaries
      2. for each item dictionary
        a. creates a TypeDB transaction
        b. constructs the corresponding TypeQL insert query
        c. runs the query
        d. commits the transaction
      :param input as dictionary: contains details required to parse the data
      :param session: off of which a transaction will be created
    """
    items = parse_data_to_dictionaries(input)  # 1

    for item in items:  # 2
        with session.transaction(TransactionType.WRITE) as transaction:  # a
            graql_insert_query = input["template"](item)  # b
            print("Executing TypeQL Query: " + graql_insert_query)
            transaction.query().insert(graql_insert_query)  # c
            transaction.commit()  # d

    print("\nInserted " + str(len(items)) +
          " items from [ " + input["file"] + ".csv] into TypeDB.\n")


def company_template(company):
    return 'insert $company isa company, has name "' + company["name"] + '";'


def person_template(person):
    # insert person
    graql_insert_query = 'insert $person isa person, has phone-number "' + \
        person["phone_number"] + '"'
    if person["first_name"] != "":
        graql_insert_query += ', has first-name "' + person["first_name"] + '"'
        graql_insert_query += ', has last-name "' + person["last_name"] + '"'
        graql_insert_query += ', has city "' + person["city"] + '"'
        graql_insert_query += ", has age " + str(person["age"])
    graql_insert_query += ";"
    return graql_insert_query


def contract_template(contract):
    # match company
    graql_insert_query = 'match $company isa company, has name "' + \
        contract["company_name"] + '";'
    # match person
    graql_insert_query += ' $customer isa person, has phone-number "' + \
        contract["person_id"] + '";'
    # insert contract
    graql_insert_query += " insert (provider: $company, customer: $customer) isa contract;"
    return graql_insert_query


def call_template(call):
    # match caller
    graql_insert_query = 'match $caller isa person, has phone-number "' + \
        call["caller_id"] + '";'
    # match callee
    graql_insert_query += ' $callee isa person, has phone-number "' + \
        call["callee_id"] + '";'
    # insert call
    graql_insert_query += (" insert $call(caller: $caller, callee: $callee) isa call; " +
                           "$call has started-at " + call["started_at"] + "; " +
                           "$call has duration " + str(call["duration"]) + ";")
    return graql_insert_query


def parse_data_to_dictionaries(input):
    """
      1. reads the file through a stream,
      2. adds the dictionary to the list of items
      :param input.file as string: the path to the data file, minus the format
      :returns items as list of dictionaries: each item representing a data item from the file at input.file
    """
    items = []
    with open(input["file"] + ".csv") as data:  # 1
        for row in csv.DictReader(data, skipinitialspace=True):
            item = {key: value for key, value in row.items()}
            items.append(item)  # 2
    return items


Inputs = [
    {
        "file": "companies",
        "template": company_template
    },
    {
        "file": "people",
        "template": person_template
    },
    {
        "file": "contracts",
        "template": contract_template
    },
    {
        "file": "calls",
        "template": call_template
    }
]

if __name__ == "__main__":
    build_phone_call_graph(inputs=Inputs, data_path="../../datasets/phone-calls/", keyspace_name = "phone_calls")
