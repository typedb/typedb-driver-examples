# the Python client for Grakn
# https://github.com/graknlabs/grakn/tree/master/client-python
import grakn
# Python's built in module for dealing with .csv files.
# we will use it read data source files.
# https://docs.python.org/3/library/csv.html#dialects-and-formatting-parameters
import csv


def build_phone_call_graph(inputs):
    '''
      gets the job done:
      1. creates a Grakn instance
      2. creates a session to the targeted keyspace
      3. loads the csv data to Grakn for each file
      4. closes the session
      :param input as list of dictionaties: each dictionary contains details required to parse the data
    '''
    client = grakn.Grakn(uri="localhost:48555")  # 1
    with client.session(keyspace="phone_calls") as session:  # 2 and 4
        for input in inputs:
            print("Loading from [" + input["data_path"] + "] into Grakn ...")
            load_data_into_grakn(input, session)  # 3


def load_data_into_grakn(input, session):
    '''
      loads the csv data into our Grakn phone_calls keyspace:
      1. gets the data items as a list of dictionaries
      2. for each item dictionary
        a. creates a Grakn transaction
        b. constructs the corresponding Graql insert query
        c. runs the query
        d. commits the transaction
      :param input as dictionary: contains details required to parse the data
      :param session: off of which a transaction will be created
    '''
    items = parse_data_to_dictionaries(input)  # 1

    for item in items:  # 2
        with session.transaction(grakn.TxType.WRITE) as tx:  # a
            graql_insert_query = input["template"](item)  # b
            print("Executing Graql Query: " + graql_insert_query)
            tx.query(graql_insert_query)  # c
            tx.commit()  # d

    print("\nInserted " + str(len(items)) +
          " items from [ " + input["data_path"] + "] into Grakn.\n")


def company_template(company):
    return 'insert $company isa company, has name "' + company["name"] + '";'


def person_template(person):
    # insert person
    graql_insert_query = 'insert $person isa person, has phone-number "' + \
        person["phone_number"] + '"'
    if person["first_name"] == "":
        # person is not a customer
        graql_insert_query += ", has is-customer false"
    else:
        # person is a customer
        graql_insert_query += ", has is-customer true"
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
    '''
      1. reads the file through a stream,
      2. adds the dictionary to the list of items
      :param input.data_path as string: the path to the data file, minus the format
      :returns items as list of dictionaries: each item representing a data item from the file at input.data_path
    '''
    items = []
    with open(input["data_path"] + ".csv") as data:  # 1
        for row in csv.DictReader(data, skipinitialspace=True):
            item = {key: value for key, value in row.items()}
            items.append(item)  # 2
    return items


Inputs = [
    {
        "data_path": "python/migration/csv/files/phone-calls/data/companies",
        "template": company_template
    },
    {
        "data_path": "python/migration/csv/files/phone-calls/data/people",
        "template": person_template
    },
    {
        "data_path": "python/migration/csv/files/phone-calls/data/contracts",
        "template": contract_template
    },
    {
        "data_path": "python/migration/csv/files/phone-calls/data/calls",
        "template": call_template
    }
]

if __name__ == "__main__":
    build_phone_call_graph(inputs=Inputs)
