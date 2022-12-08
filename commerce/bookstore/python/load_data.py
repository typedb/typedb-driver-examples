import csv, uuid, random
import sys

from typedb.client import TypeDB, SessionType, TransactionType
import argparse

# Verbosity option implementation
parser = argparse.ArgumentParser(description='Loads data into TypeDB for the Bookstore example')
parser.add_argument("-v", "--verbose", "-d", "--debug", help='Increase output verbosity',
                    dest="verbosity", action='store_true')
args = vars(parser.parse_args())

if args["verbosity"]:  # if the argument was set
    print("High verbosity option turned on.")
    debug = True  # Shows verbose debug messages in the console output
else:
    debug = False  # No debug messages

data_path = "data/"  # path to csv files to import/load data
db = "bookstore"  # Name of the DB to connect on the TypeDB


def parse_data_to_dictionaries(input):
    """
      :param input.file as string: the path to the data file, minus the format
      :returns items as list of dictionaries: each item representing a data item from the file at input.file
    """
    if debug: print("Parsing of " + input["file"] + "started.")
    items = []

    with open(input["file"] + ".csv", encoding="UTF-8") as data:  # reads the file through a stream,
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
            typeql_insert_query = input["template"](item)  # b # This calls one of the _template functions to
            # construct the corresponding TypeQL insert query
            if debug: print("Executing TypeQL Query: " + typeql_insert_query)
            transaction.query().insert(typeql_insert_query)  # runs the query
            transaction.commit()  # commits the transaction

    print("Inserted " + str(len(items)) +
          " items from [ " + input["file"] + ".csv] into TypeDB.\n")
    return  # END of load_data_into_typedb()


def books_generate_query(book):  # building a TypeQL request to insert a book
    return "insert $b isa book, has id '" + str(uuid.uuid4()) + "', has ISBN '" + book["ISBN"] + "', has name '" \
           + book["Book-Title"] + "', has book-author '" + book["Book-Author"] + "', has publisher '" \
           + book["Publisher"] + "', has price " + str(random.randint(3, 100)) + ", has stock " \
           + str(random.randint(0, 25)) + ";"


def users_generate_query(user):  # building a TypeQL request to insert a user
    first_names = ("John", "Andy", "Joe", "Bob", "Alex", "Mary", "Alexa", "Monika", "Vladimir", "Tom", "Jerry")
    typeql_insert_query = "insert $u isa user, has id '" + str(uuid.uuid4()) + "', has foreign-id '" + user["User-ID"] + "'"
    if user["Age"] != "NULL":  # Check the data before loading it
        typeql_insert_query += ",  has age " + user["Age"]  # If we have Age data in the file - we will use it
    else:  # Additional logic for missing data: in this case — we generate random values
        typeql_insert_query += ",  has age " + str(random.randint(18, 105))  # Add random age
    typeql_insert_query += ", has name '" + random.choice(first_names) + "';"  # Add random name

    return typeql_insert_query


def ratings_generate_query(review):  # building a TypeQL request to insert a review (reviewing relation)
    typeql_insert_query = "match $u isa user, has foreign-id '" + review["User-ID"] + "'; " \
                          "$b isa book, has ISBN '" + review["ISBN"] + "'; " \
                          "insert $r (author: $u, product: $b) isa reviewing;" \
                          "$r has rating " + review["Book-Rating"] + ";"

    return typeql_insert_query


def genre_generate_query(genre):  # building a TypeQL request to insert a genre/book association

    typeql_insert_query = "match $b isa book, has ISBN '" + genre["ISBN"] + "'; " \
                          "$g isa genre; $g '" + genre["Genre"] + "'; " \
                          "insert $tag (tag: $g, book: $b) isa taging;"

    return typeql_insert_query


def orders_generate_query(order):  # building a TypeQL request to insert an order
    typeql_insert_query = "insert $o isa order, has id '" + order["id"] + "'," \
                          "has foreign-user-id '" + order["User-ID"] + "', " \
                          "has date " + order["date"] + ", " \
                          "has status '" + order["status"] + "'," \
                          "has delivery-address '" + order["delivery_address"] + "', " \
                          "has payment-details '" + order["payment_details"] + "';"

    return typeql_insert_query


def generate_ordered_items():  # Generating random item-lists for orders from books
    result = []
    # generate 5 random sets of 2-9 books
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                typeql_read_query = "match $b isa book, has ISBN $x; get $x; limit 800;"  # get 800 books
                if debug: print("Executing TypeQL read Query: " + typeql_read_query)
                iterator = transaction.query().match(typeql_read_query)  # Execute read query
                answers = [ans.get("x") for ans in iterator]
                books = [answer.get_value() for answer in answers]  # This contains the result (800 ISBN records)
                for order_id in range(1,6):  # Go through all 5 orders
                    ordered_books = []
                    for item_n in range(1, random.randint(2, 10)):  #
                        ordered_books.append(books[random.randint(0, 799)])  # Exactly 800 books to select from
                    result.append(ordered_books)

    n = 1
    # Add ordering relations (assign items from the sets above)
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            for order in result:
                if debug: print("\nOrder #", n, "contains:")
                for book in order:
                    if debug: print("\nISBN", book)
                    with session.transaction(TransactionType.WRITE) as transaction:
                        typeql_insert_query = "match $b isa book, has ISBN '" + book + "';" \
                                              "$o isa order, has id '" + str(n) + "', has foreign-user-id $fui;" \
                                              "$u isa user, has foreign-id $fi;" \
                                              "$fui = $fi;" \
                                              "insert (order: $o, item: $b, author: $u ) isa ordering;"
                                              # the $fui and $fi variables are compared by value only
                        if debug: print("Executing TypeQL Query: " + typeql_insert_query)
                        transaction.query().insert(typeql_insert_query)
                        transaction.commit()
                n += 1
    return  # END of generate_ordered_items()


def create_genre_tags():  # Creating genre tags and tag hierarchy

    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.WRITE) as transaction:
                transaction.query().insert("insert $g 'Fiction' isa genre;")
                transaction.query().insert("insert $g 'Non fiction' isa genre;")
                transaction.query().insert("insert $g 'Other' isa genre;")
                transaction.query().insert("insert $g 'Adults only' isa genre;")
                transaction.query().insert("insert $g 'Kids friendly' isa genre;")
                transaction.query().insert("insert $g 'Sci-Fi' isa genre;")
                transaction.query().insert("match $b = 'Sci-Fi'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Fantasy' isa genre;")
                transaction.query().insert("match $b = 'Fantasy'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Biography' isa genre;")
                transaction.query().insert("match $b = 'Biography'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Adventure' isa genre;")
                transaction.query().insert("match $b = 'Adventure'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Detective_story' isa genre;")
                transaction.query().insert("match $b = 'Detective_story'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'History' isa genre;")
                transaction.query().insert("match $b = 'History'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Politics' isa genre;")
                transaction.query().insert("match $b = 'Politics'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Up to 5 years' isa genre;")
                transaction.query().insert("match $b = 'Up to 5 years'; $b isa genre;"
                                           "$p = 'Kids friendly'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Technical Documentation' isa genre;")
                transaction.query().insert("match $b = 'Technical Documentation'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("match $b = 'Technical Documentation'; $b isa genre;"
                                           "$p = 'Adults only'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Map' isa genre;")
                transaction.query().insert("match $b = 'Map'; $b isa genre;"
                                           "$p = 'Technical Documentation'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.commit()
    print("Created genre tags.")
    return


def load_data():  # Main data load function
    create_genre_tags()  # Creating genre tags before loading data
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            for input in Inputs:  # Iterating through all CSV files
                input["file"] = data_path + input["file"]
                if debug: print("Loading from [" + input["file"] + ".csv] into TypeDB ...")
                load_data_into_typedb(input, session)  # Main data loading function. Repeat for only file in Inputs
            generate_ordered_items()  # Add randomly generated lists of items into orders
            print("\nData loading complete!")
    return


def has_existing_data():  # Checking whether the DB has schema and data already
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.SCHEMA) as session:
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
        with client.session(db, SessionType.SCHEMA) as session:
            with open("../schema.tql", "r") as schema:  # Read the schema.tql file
                define_query = schema.read()
                with session.transaction(TransactionType.WRITE) as transaction:
                    try:
                        transaction.query().define(define_query)  # Execute query to load the schema
                        transaction.commit()  # Commit transaction
                        print("Loaded the " + db + " schema.")
                        return True  # Setup complete
                    except Exception as e:
                        print("Failed to load schema: " + str(e))
                        return False  # Setup failed


# This is a list of files to import data from and corresponding functions to load the parsed data into the DB
Inputs = [
    {
        "file": "books",
        "template": books_generate_query
    },
    {
        "file": "users",
        "template": users_generate_query
    },
    {
        "file": "ratings",
        "template": ratings_generate_query
    },
    {
        "file": "orders",
        "template": orders_generate_query
    },
    {
        "file": "genres",
        "template": genre_generate_query
    }
]

# This is the main body of this script
with TypeDB.core_client("localhost:1729") as client:
    if client.databases().contains(db):  # Check the DB existence
        print("Detected DB " + db + ". Connecting.")
        if not has_existing_data():  # Most likely the DB is empty and has no schema
            print("Attempting to load the schema and data.")
            if setup():  # Schema has been loaded
                load_data()  # Main data loading function
        else:  # The data check showed that we already have schema and some data in the DB
            print("To reload data we will delete the existing DB... Please confirm!")
            if input("Type in Delete to proceed with deletion: ") == "delete" or "Delete" or "DELETE":
                client.databases().get(db).delete()  # Deleting the DB
                print("Deleted DB " + db + ".")
                client.databases().create(db)  # Creating new (empty) DB
                print("DB " + db + " created. Applying schema...")
                if setup():  # Schema has been loaded
                    load_data()  # Main data loading function
            else:
                exit("Database was not deleted due to user choice. Exiting.")

    else:  # DB is non-existent
        print("DB " + db + " is absent. Trying to create.")
        client.databases().create(db)  # Creating the DB
        print("DB " + db + " created. Applying schema...")
        if setup():  # Schema has been loaded
            load_data()  # Main data loading function
