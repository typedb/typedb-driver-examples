# the Python client for Grakn
# https://github.com/graknlabs/client-python
from grakn.client import GraknClient

# an iterative JSON parser
# we will use it read data source files.
# https://pypi.org/project/ijson/
import ijson


def build_phone_call_graph(inputs, data_path, keyspace_name):
    """
      gets the job done:
      1. creates a Grakn instance
      2. creates a session to the targeted keyspace
      3. for each input:
        - a. constructs the full path to the data file
        - b. loads csv to Grakn
      :param input as list of dictionaties: each dictionary contains details required to parse the data
    """
    with GraknClient(uri="localhost:48555") as client:  # 1
        with client.session(keyspace=keyspace_name) as session:  # 2
            for input in inputs:
                input["file"] = input["file"].replace(data_path, "")  # for testing purposes
                input["file"] = data_path + input["file"]  # 3a
                print("Loading from [" + input["file"] + ".csv] into Grakn ...")
                load_data_into_grakn(input, session)  # 3b


def load_data_into_grakn(input, session):
    '''
      loads the json data into our Grakn phone_calls keyspace:
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
        with session.transaction().write() as transaction:  # a
            graql_insert_query = input["template"](item)  # b
            print("Executing Graql Query: " + graql_insert_query)
            transaction.query(graql_insert_query)  # c
            transaction.commit()  # d

    print("\nInserted " + str(len(items)) +
          " items from [ " + input["file"] + ".json] into Grakn.\n")


def company_template(company):
    return 'insert $company isa company, has name "' + company["name"] + '";'


def person_template(person):
    # insert person
    graql_insert_query = 'insert $person isa person, has phone-number "' + \
        person["phone_number"] + '"'
    if "first_name" in person:
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
      :param input.file as string: the path to the data file, minus the format
      :returns items as list of dictionaries: each item representing a data item from the file at input.file
    '''
    items = []
    with open(input["file"] + ".json") as data:  # 1
        for item in ijson.items(data, "item"):
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
