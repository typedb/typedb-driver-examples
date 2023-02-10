#
# Copyright (C) 2023 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

import csv
import os
from typedb.client import TypeDB, SessionType, TransactionType
import loaders
import config
import argparse

# Verbosity option implementation
parser = argparse.ArgumentParser(description='Loads data into TypeDB for the Bookstore example')
parser.add_argument("-v", "--verbose", "-d", "--debug", help='Increase output verbosity',
                    dest="verbose", action='store_true')
args = vars(parser.parse_args())

if args["verbose"]:  # if the argument was set
    print("High verbosity option turned on.")
    debug = True  # Shows verbose debug messages in the console output
else:
    debug = False  # No debug messages


def parse_data_to_dictionaries(input):  # input.file is a string: the path to the dataset file
    if debug: print("Parsing of " + input["file"] + "started.")
    items = []  # prepare an empty list
    with open(input("").file, encoding="UTF-8") as data:  # reads the dataset file through a stream
        for row in csv.DictReader(data, delimiter=";", skipinitialspace=True):  # iterate through rows
            item = {key: value for key, value in row.items()}  # Creates an item. Keys are taken from the first row
            items.append(item)  # adds the dictionary to the list of items
    if debug: print("Parsing of " + input["file"] + " successful.")
    return items  # items as list of dictionaries: each item representing a data item from the file at loader.file


def load_data_into_typedb(loader, session):  # Requests generation of insert queries and sends queries to the TypeDB
    """
      :param loader as class: has load method to build insert query. Object initiated with an item to insert
      :param session: an established connection to the TypeDB off of which a transaction will be created
    """
    items = parse_data_to_dictionaries(loader)  # parses csv file (loader.file) to create a list of dictionaries
    skip_count = 0  # counter of non-successful insert attempts
    for item in items:  # for each item dictionary in the list (former row in csv file)
        with session.transaction(TransactionType.WRITE) as transaction:  # Open transaction to write with session provided
            input_object = loader(item)  # This is an object of one of the Loader subclass initiated with an item
            typeql_insert_query = input_object.load()  # This builds the corresponding TypeQL insert query from item
            if typeql_insert_query != "":
                if debug: print("Executing TypeQL Query: " + typeql_insert_query)
                transaction.query().insert(typeql_insert_query)  # runs the query
                transaction.commit()  # commits the transaction
                # todo: Add a transaction result check. Increase skip_cont if nothing was inserted
            else:
                if debug: print("Item parsing resulted in empty query statement. Skipping this item —", item)
                skip_count += 1
    loaded_count = len(items) - skip_count
    print("Inserted " + str(loaded_count) + " out of " + str(len(items)) + " items from [ "
          + input_object.file + "] into TypeDB with", loader.__name__)
    return loaded_count  # END of load_data_into_typedb()


def load_data():  # Main data load function
    res = []
    with TypeDB.core_client(config.typedb_server_addr) as client:  # Establishing connection
        with client.session(config.db, SessionType.DATA) as session:  # Access data in the database
            for loader in loaders.loaders_list:  # Iterating through the list of classes to import all data
                if debug: print("Loading from [" + loader("").file + "] into TypeDB ...")
                res.append(load_data_into_typedb(loader, session))  # Call loader to load data with session
            print("\nData loading complete!")
            if debug: print("We have inserted the following nu,ber of items:", res)
    return res


def has_existing_data():  # Checking whether the DB already has the schema and the data loaded
    with TypeDB.core_client(config.typedb_server_addr) as client:  # Establishing connection
        with client.session(config.db, SessionType.SCHEMA) as session:  # Access data in the database
            with session.transaction(TransactionType.READ) as transaction:  # Open transaction to read
                try:
                    typeql_read_query = "match $b isa book, has ISBN $x; get $x; limit 3;"
                    transaction.query().match(typeql_read_query)
                    print("The DB contains the schema and loaded data already.")
                    return True  # Success means DB most likely already has the schema and the data loaded
                except:
                    return False  # Exception — we consider DB as empty (brand new, no schema, no data)


def load_schema():  # Loading schema
    this_script_dir = os.path.dirname(__file__)  # Look for a path to this script, load_data.py
    with TypeDB.core_client(config.typedb_server_addr) as client:  # Establishing connection
        with client.session(config.db, SessionType.SCHEMA) as session:  # Access data in the database
            with open(os.path.join(this_script_dir, "../schema.tql"), "r") as schema:  # Read the schema.tql file
                define_query = schema.read()
                with session.transaction(TransactionType.WRITE) as transaction:  # Open transaction to write
                    try:
                        transaction.query().define(define_query)  # Execute query to load the schema
                        transaction.commit()  # Commit transaction
                        print("Loaded the " + config.db + " schema.")
                        return True  # Setup complete
                    except Exception as e:
                        print("Failed to load schema: " + str(e))
                        return False  # Setup failed


def main():  # This is the main function of this script
    with TypeDB.core_client(config.typedb_server_addr) as client:  # Establishing connection
        if client.databases().contains(config.db):  # Check the DB existence
            print("Detected DB " + config.db + ". Connecting.")
            if not has_existing_data():  # Most likely the DB is empty and has no schema
                print("Attempting to load the schema and data.")
                if load_schema():  # Schema has been loaded
                    load_data()  # Main data loading function
            else:  # The data check showed that we already have schema and some data in the DB
                print("To reload data we will delete the existing DB... Please confirm!")
                if input("Type in Delete to proceed with deletion: ") == "delete" or "Delete" or "DELETE":
                    client.databases().get(config.db).delete()  # Deleting the DB
                    print("Deleted DB " + config.db + ".")
                    client.databases().create(config.db)  # Creating new (empty) DB
                    print("DB " + config.db + " created. Applying schema...")
                    if load_schema():  # Schema has been loaded
                        return load_data()  # Main data loading function
                else:
                    exit("Database was not deleted due to user choice. Exiting.")
                    return False

        else:  # DB is non-existent
            print("DB " + config.db + " is absent. Trying to create.")
            client.databases().create(config.db)  # Creating the DB
            print("DB " + config.db + " created. Applying schema...")
            if load_schema():  # Schema has been loaded
                return load_data()  # Main data loading function

    return False


if __name__ == '__main__':
    main()
