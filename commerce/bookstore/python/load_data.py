import csv
from typedb.client import TypeDB, SessionType, TransactionType
import loaders
import config
import argparse

# Verbosity option implementation
parser = argparse.ArgumentParser(description="Loads data into TypeDB for the Bookstore example")
parser.add_argument("-v", "--verbose", "-d", "--debug", help="Increase output verbosity",
                    dest="verbosity", action="store_true")
args = vars(parser.parse_args())

if args["verbosity"]:  # if the argument was set
    print("High verbosity option turned on.")
    debug = True  # Shows verbose debug messages in the console output
else:
    debug = False  # No debug messages


def parse_data_to_dictionaries(input):
    """
      :param input.file as string: the path to the data file, minus the format
      :returns items as list of dictionaries: each item representing a data item from the file at input.file
    """
    if debug: print("Parsing of " + input["file"] + "started.")
    items = []

    with open(input("").file, encoding="UTF-8") as data:  # reads the file through a stream,
        for row in csv.DictReader(data, delimiter=";", skipinitialspace=True):
            item = {key: value for key, value in row.items()}  # fieldnames (keys) are taken from the first row
            items.append(item)  # adds the dictionary to the list of items
    if debug: print("Parsing of " + input["file"] + " successful.")
    return items


def load_data_into_typedb(input, session):  # Requests generation of insert queries and sends them to TypeDB
    """
      :param input as dictionary: contains details required to parse the data
      :param session: off of which a transaction will be created
    """
    items = parse_data_to_dictionaries(input)  # gets the data items as a list of dictionaries
    for item in items:  # for each item dictionary
        with session.transaction(TransactionType.WRITE) as transaction:  # creates a TypeDB transaction
            i = input(item)  # This is an object of input type with an item as a parameter
            typeql_insert_query = i.load()  # This calls one of the _generate_query functions to
            # construct the corresponding TypeQL insert query
            if debug: print("Executing TypeQL Query: " + typeql_insert_query)
            transaction.query().insert(typeql_insert_query)  # runs the query
            transaction.commit()  # commits the transaction

    print("Inserted " + str(len(items)) + " items from [ " + i.file + "] into TypeDB.\n")
    return  # END of load_data_into_typedb()


def load_data():  # Main data load function
    loaders.create_genre_tags()  # Creating genre tags before loading data
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.DATA) as session:
            for input_type in loaders.Input_types_list:  # Iterating through all types of import
                r = input_type("")  # default object of the input_type
                if debug: print("Loading from [" + r.file + "] into TypeDB ...")
                load_data_into_typedb(input_type, session)  # Main data loading function. Repeat for only file in Inputs
            # loaders.generate_ordered_items()  # Add randomly generated lists of items into orders
            print("\nData loading complete!")
    return


def has_existing_data():  # Checking whether the DB has schema and data already
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.SCHEMA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                try:
                    typeql_read_query = "match $b isa book, has ISBN $x; get $x; limit 3;"
                    transaction.query().match(typeql_read_query)
                    print("The DB contains the schema and loaded data already.")
                    return True
                except:  # If the attempt was unsuccessful — we consider DB as empty (brand new, no schema)
                    return False


def setup():  # Loading schema
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.SCHEMA) as session:
            with open("../schema.tql", "r") as schema:  # Read the schema.tql file
                define_query = schema.read()
                with session.transaction(TransactionType.WRITE) as transaction:
                    try:
                        transaction.query().define(define_query)  # Execute query to load the schema
                        transaction.commit()  # Commit transaction
                        print("Loaded the " + config.db + " schema.")
                        return True  # Setup complete
                    except Exception as e:
                        print("Failed to load schema: " + str(e))
                        return False  # Setup failed

# This is the main body of this script
with TypeDB.core_client("localhost:1729") as client:
    if client.databases().contains(config.db):  # Check the DB existence
        print("Detected DB " + config.db + ". Connecting.")
        if not has_existing_data():  # Most likely the DB is empty and has no schema
            print("Attempting to load the schema and data.")
            if setup():  # Schema has been loaded
                load_data()  # Main data loading function
        else:  # The data check showed that we already have schema and some data in the DB
            print("To reload data we will delete the existing DB... Please confirm!")
            if input("Type in Delete to proceed with deletion: ") == "delete" or "Delete" or "DELETE":
                client.databases().get(config.db).delete()  # Deleting the DB
                print("Deleted DB " + config.db + ".")
                client.databases().create(config.db)  # Creating new (empty) DB
                print("DB " + config.db + " created. Applying schema...")
                if setup():  # Schema has been loaded
                    load_data()  # Main data loading function
            else:
                exit("Database was not deleted due to user choice. Exiting.")

    else:  # DB is non-existent
        print("DB " + config.db + " is absent. Trying to create.")
        client.databases().create(config.db)  # Creating the DB
        print("DB " + config.db + " created. Applying schema...")
        if setup():  # Schema has been loaded
            load_data()  # Main data loading function
